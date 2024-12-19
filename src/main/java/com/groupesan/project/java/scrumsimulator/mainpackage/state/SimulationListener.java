package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerObject;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;

import java.util.HashMap;
import java.util.UUID;

/**
 * Interface for the simulation listener
 */
public interface SimulationListener {
    void onProgressUpdate(int progressValue, int day, int sprint, int sprintDuration);
    void onBlockerDetected(BlockerObject blocker);
    void onBlockerResolved(BlockerObject blocker);
    void onUserStory(UserStory userStory);
    void onSimulationStopped();
    void onSimulationStarted();
    void onSprintCompletion();
    void onInProgressUserStory();

    void onChartChange(Integer day, Double point);
    void onUserStoryStatusChange(UserStory userStory);
}
