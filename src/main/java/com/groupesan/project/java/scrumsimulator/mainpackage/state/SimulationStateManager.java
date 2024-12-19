package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerObject;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerSolution;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.Simulation;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.Sprint;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;
import com.groupesan.project.java.scrumsimulator.mainpackage.utils.RandomUtils;

/**
 * SimulationStateManager manages the state of a simulation, including whether
 * it is running and saving its ID.
 */
public class SimulationStateManager {

    public enum SprintStateEnum {
        RUNNING,
        STOPPED,
        PAUSED
    }

    private Simulation currentSimulation;
    private SprintStateEnum state;
    private Integer day;
    private Integer sprint;
    private Integer progressValue;

    private static final double WORK_RATE_PER_DAY = 3;

    private static SimulationStateManager instance;
    private final List<SimulationListener> listeners = new ArrayList<>();

    private SimulationStateManager() {
        init();
    }

    private void init() {
        state = SprintStateEnum.STOPPED;
        day = 1;
        sprint = 1;
        progressValue = 0;
    }

    public int getSprintNum() {
        return sprint;
    }

    /**
     * Returns the singleton instance of SimulationStateManager.
     *
     * @return the singleton instance
     */
    public static synchronized SimulationStateManager getInstance() {
        if (instance == null) {
            instance = new SimulationStateManager();
        }
        return instance;
    }

    /**
     * Sets the current simulation.
     *
     * @param simulation The simulation to set as the current simulation.
     */
    public void setCurrentSimulation(Simulation simulation) {
        this.currentSimulation = simulation;
    }

    /**
     * Gets the current simulation
     *
     * @return current simulation
     */
    public Simulation getCurrentSimulation() {
        return this.currentSimulation;
    }

    public void addListener(SimulationListener listener) {
        listeners.add(listener);
    }

    public void removeListener(SimulationListener listener) {
        listeners.remove(listener);
    }

    private void notifyProgressUpdate() {
        for (SimulationListener listener : listeners) {
            listener.onProgressUpdate(progressValue, day, sprint, currentSimulation.getSprintDuration());
        }
    }

    private void notifyBlockerDetected(BlockerObject blocker) {
        for (SimulationListener listener : listeners) {
            listener.onBlockerDetected(blocker);
        }
    }

    private void notifyBlockerResolved(BlockerObject blocker) {
        for (SimulationListener listener : listeners) {
            listener.onBlockerResolved(blocker);
        }
    }

    private void notifySimulationStarted() {
        for (SimulationListener listener : listeners) {
            listener.onSimulationStarted();
        }
    }

    private void notifySimulationStopped() {
        for (SimulationListener listener : listeners) {
            listener.onSimulationStopped();
        }
    }

    private void notifyUserStory(UserStory userStory) {
        for (SimulationListener listener : listeners) {
            listener.onUserStory(userStory);
        }
    }

    private void notifyInProgressUserStory() {
        for (SimulationListener listener : listeners) {
            listener.onInProgressUserStory();
        }
    }

    private void notifyResetUserStoryPanel() {
        for (SimulationListener listener : listeners) {
            listener.onSprintCompletion();
        }
    }

    private void notifyStoryStatusChange(UserStory userStory) {
        for (SimulationListener listener : listeners) {
            listener.onUserStoryStatusChange(userStory);
        }
    }

    /**
     * Burn down chart implementation. Delete if necessary!
     * @param selectedStory
     */
    private void notifyChartChange(UserStory selectedStory) {


        for (SimulationListener listener : listeners) {
            listener.onChartChange(day, selectedStory.getPointValue());
        }
    }

    public void startSimulation() {
        if (currentSimulation == null) {
            JOptionPane.showMessageDialog(null, "No simulation selected");
            return;
        }

        day = 1;
        sprint = 1;
        progressValue = 0;

        for (Sprint sprint : currentSimulation.getSprints()) {
            for (UserStory userStory : sprint.getUserStories()) {
                userStory.removeAllBlockers();
            }
        }

        state = SprintStateEnum.RUNNING;
        RandomUtils.resetInstance(currentSimulation.getRandomSeed());

        notifySimulationStarted();
        new Thread(this::runSimulation).start();
    }

    public void stopSimulation() {

        if (currentSimulation == null) {
            JOptionPane.showMessageDialog(null, "No simulation selected");
            return;
        }

        init();
        notifySimulationStopped();
        SimulationSingleton.getInstance().saveSimulationDetails();

    }

    private void runSimulation() {
        addUserStory();
        detectInProgressUserStory();
        //getTotalPointValue();
        while (true) {

            try {
                for (int i = 0; i < 10; i++) {
                    Thread.sleep(100);
                    if (state == SprintStateEnum.STOPPED) {
                        return;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (state == SprintStateEnum.PAUSED) {
                continue;
            }
            progressValue = (int) Math.round(100.0 / currentSimulation.getSprintDuration() * day);

            progressStories();
            notifyProgressUpdate();

            resolveBlockers();
            detectBlockers();

            if (sprint >= currentSimulation.getSprintCount() && day >= currentSimulation.getSprintDuration()) {

                stopSimulation();
            } else {
                day++;
                if (day > currentSimulation.getSprintDuration()) {
                    //
                    day = 1;
                    sprint++;
                    resetPanel(); // Reset the panels to clear out stories from previous sprints, regardless if
                                  // they're completed or not.
                    addUserStory(); // Add the user stories from the new sprint
                    detectInProgressUserStory(); // Get the stories and detect if they're in progress
                    //getTotalPointValue(); // Get total point value
                }
            }
        }
    }

    private void resetPanel() {
        notifyResetUserStoryPanel();
    }

    private void addUserStory() {
        for (UserStory userStory : currentSimulation.getSprints().get(sprint - 1).getUserStories()) {
            userStory.changeState(new UserStoryNewState(userStory));
            notifyUserStory(userStory);
        }
    }

    public void progressStories() {

        int numStories = currentSimulation.getSprints().get(sprint - 1).getUserStories().size();
        int numDays = currentSimulation.getSprintDuration();
        int numSelectedStories = (int) Math.round((numStories * WORK_RATE_PER_DAY) / numDays);
        if (numSelectedStories == 0) {
            numSelectedStories = 1;
        }

        try {
            List<UserStory> usList = currentSimulation.getSprints().get(sprint - 1).getUserStories();

            List<UserStory> userStoriesToProgress = new ArrayList<>();
            for (UserStory userStory : usList) {
                if (!(userStory.getUserStoryState() instanceof UserStoryNewState)
                        && !(userStory.getUserStoryState() instanceof UserStoryCompletedState)
                        && userStoriesToProgress.size() < numSelectedStories) {
                    userStoriesToProgress.add(userStory);
                }
            }

            if (userStoriesToProgress.size() < numSelectedStories) {
                for (UserStory userStory : usList) {
                    if (userStory.getUserStoryState() instanceof UserStoryNewState
                            && userStoriesToProgress.size() < numSelectedStories) {
                        userStoriesToProgress.add(userStory);
                    }
                }
            }

            for (UserStory selectedStory : userStoriesToProgress) {
                if (selectedStory.getUserStoryState() instanceof UserStoryUnselectedState) {
                    selectedStory.changeState(new UserStoryNewState(selectedStory));
                } else if (selectedStory.getUserStoryState() instanceof UserStoryNewState) {
                    selectedStory.changeState(new UserStoryInProgressState(selectedStory));
                } else if (selectedStory.getUserStoryState() instanceof UserStoryInProgressState) {
                    selectedStory.changeState(new UserStoryTestState(selectedStory));
                } else if (selectedStory.getUserStoryState() instanceof UserStoryTestState &&
                        !(selectedStory.getUserStoryState() instanceof UserStoryBlockedState)) {
                    selectedStory.changeState(new UserStoryCompletedState(selectedStory));
                    notifyChartChange(selectedStory);
                }
                notifyStoryStatusChange(selectedStory);
            }
        } catch (IllegalArgumentException ie) {
            // The code should detect the lack of assigned user stories from the backlog and
            // send a message, before closing the simulation
            // ie.printStackTrace();
        }
    }


    /**
     * Detect the state of all user stories as the simulation is in progress.
     */
    private void detectInProgressUserStory() {
        notifyInProgressUserStory();
    }

    private void detectBlockers() {
        for (UserStory userStory : currentSimulation.getSprints().get(sprint - 1).getUserStories()) {
            boolean alreadyCompleted = !(userStory.getUserStoryState() instanceof UserStoryCompletedState); // check if the user story is completed. Blockers cannot be reintroduced if it's completed
            boolean inProgress = (userStory.getUserStoryState() instanceof UserStoryInProgressState); // check if the user story is in progress. A new user story cannot immediately have a blocker
            if (alreadyCompleted && inProgress) {
                BlockerObject blocker = SimulationStateManager.getInstance().getCurrentSimulation().rollForBlocker();
                if (blocker != null && !(userStory.getUserStoryState() instanceof UserStorySpikedState)) {
                    notifyBlockerDetected(blocker);
                    userStory.setBlocker(blocker);
                    userStory.changeState(new UserStoryBlockedState(userStory));
                    notifyStoryStatusChange(userStory);
                }

            }
        }
    }

    private void resolveBlockers() {
        for (UserStory userStory : currentSimulation.getSprints().get(sprint - 1).getUserStories()) {
            for (BlockerObject blocker : userStory.getBlockers()) {
                if (blocker == null || blocker.isResolved()) {
                    continue;
                }

                if (blocker.attemptResolve()) {
                    blocker.resolve();
                    userStory.changeState(new UserStoryTestState(userStory));
                    notifyStoryStatusChange(userStory);
                    notifyBlockerResolved(blocker);
                }
                if (blocker.getState() == BlockerObject.BlockerState.SPIKED) {
                    userStory.changeState(new UserStorySpikedState(userStory));
                    notifyStoryStatusChange(userStory);
                }
            }
        }
    }

    public SprintStateEnum getState() {
        return state;
    }

    public void setState(SprintStateEnum state) {
        this.state = state;
    }

    public BlockerSolution getRandomBlockerSolution() {
        int totalWeight = currentSimulation.getBlockerSolutions().stream().mapToInt(BlockerSolution::getChance).sum();
        int randomValue = RandomUtils.getInstance().getRandomInt(totalWeight);

        int cumulativeWeight = 0;
        for (BlockerSolution solution : currentSimulation.getBlockerSolutions()) {
            cumulativeWeight += solution.getChance();
            if (randomValue < cumulativeWeight) {
                return solution;
            }
        }
        return null;
    }
}