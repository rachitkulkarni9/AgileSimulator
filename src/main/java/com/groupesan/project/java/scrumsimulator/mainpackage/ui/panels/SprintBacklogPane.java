package com.groupesan.project.java.scrumsimulator.mainpackage.ui.panels;

import com.groupesan.project.java.scrumsimulator.mainpackage.impl.Sprint;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager;
import com.groupesan.project.java.scrumsimulator.mainpackage.ui.utils.GridBagConstraintsBuilder;
import com.groupesan.project.java.scrumsimulator.mainpackage.ui.widgets.BaseComponent;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.List;

public class SprintBacklogPane extends JFrame implements BaseComponent {

    String selectedUserStory;
    String selectedSprintUserStory;

    List<UserStory> userStories;

    private final JFrame parent;

    public SprintBacklogPane(JFrame parent) {
        this.parent = parent;

        init();
    }

    private void updateUserStories() {
        this.userStories = SimulationStateManager.getInstance().getCurrentSimulation().getUserStories();
    }

    @Override
    public void init() {
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Container container = getContentPane();
        container.setLayout(new GridBagLayout());
        this.updateUserStories();

        setTitle("Assign Sprint Backlogs");

        if (SimulationStateManager.getInstance().getCurrentSimulation() == null) {
            JOptionPane.showMessageDialog(this,
                    "Please create and join a simulation before adding user stories to sprint backlog",
                    "No Active Simulation",
                    JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
            return;
        }
        
        JList<String> userStories = new JList<>();
        redrawUserStoriesList(userStories);
        JScrollPane userStoriesList = new JScrollPane(userStories);
        container.add(
                userStoriesList,
                new GridBagConstraintsBuilder()
                        .setGridX(0)
                        .setGridY(1)
                        .setWeightX(1)
                        .setFill(GridBagConstraints.HORIZONTAL));
        JComboBox<String> selectSprintComboBox = new JComboBox<>();
        container.add(selectSprintComboBox,
                new GridBagConstraintsBuilder()
                        .setGridX(3)
                        .setGridY(0)
                        .setWeightX(2)
                        .setFill(GridBagConstraints.HORIZONTAL));

        if (SimulationStateManager.getInstance().getCurrentSimulation() != null) {
            selectSprintComboBox.removeAllItems();
            for (Sprint sprint : SimulationStateManager.getInstance().getCurrentSimulation().getSprints()) {
                selectSprintComboBox.addItem(sprint.toString());
            }
        }

        JList<String> sprintUserStories = new JList<>();
        redrawSprintUserStoriesList(selectSprintComboBox, sprintUserStories);
        JScrollPane sprintUserStoriesList = new JScrollPane(sprintUserStories);
        container.add(
                sprintUserStoriesList,
                new GridBagConstraintsBuilder()
                        .setGridX(3)
                        .setGridY(1)
                        .setWeightX(1)
                        .setGridWidth(2)
                        .setFill(GridBagConstraints.HORIZONTAL));

        selectSprintComboBox.addActionListener(e -> {
            redrawUserStoriesList(userStories);
            redrawSprintUserStoriesList(selectSprintComboBox, sprintUserStories);
        });

        Container middleMenuContainer = new Container();
        middleMenuContainer.setLayout(new GridBagLayout());

        JButton moveLeft = getMoveLeft(selectSprintComboBox, userStories, sprintUserStories);
        JButton moveRight = getMoveRight(selectSprintComboBox, userStories, sprintUserStories);

        middleMenuContainer.add(
                moveRight,
                new GridBagConstraintsBuilder()
                        .setGridX(0)
                        .setGridY(0)
                        .setWeightX(1)
                        .setFill(GridBagConstraints.HORIZONTAL));

        middleMenuContainer.add(
                moveLeft,
                new GridBagConstraintsBuilder()
                        .setGridX(0)
                        .setGridY(1)
                        .setWeightX(1)
                        .setFill(GridBagConstraints.HORIZONTAL));

        container.add(
                middleMenuContainer,
                new GridBagConstraintsBuilder()
                        .setGridX(1)
                        .setGridY(1)
                        .setWeightX(1)
                        .setFill(GridBagConstraints.HORIZONTAL));

        JButton randomizeButton = getRandomizeButton(selectSprintComboBox, userStories, sprintUserStories);
        styleButton(randomizeButton);
        JPanel filler = new JPanel();
        container.add(filler, new GridBagConstraintsBuilder()
                        .setGridX(0)
                        .setGridY(3)
                        .setWeightY(1.0)  // This pushes everything up
                        .setGridWidth(3) 
                        .setFill(GridBagConstraints.BOTH));
        container.add(
                randomizeButton,
                new GridBagConstraintsBuilder()
                        .setGridX(3)
                        .setGridY(2)
                        .setWeightX(1)
                        .setFill(GridBagConstraints.HORIZONTAL));
        JButton cancelButton = CancelButton();
        styleButton(cancelButton);
        container.add(
                cancelButton,
                new GridBagConstraintsBuilder()
                        .setGridX(0)
                        .setGridY(7)
                        .setWeightX(1)
                        .setGridWidth(4)
                        .setFill(GridBagConstraints.HORIZONTAL));
    }

    private JButton getMoveLeft(JComboBox<String> selectSprintComboBox, JList<String> userStories,
            JList<String> sprintUserStories) {
        JButton moveLeft = new JButton("<");
        styleButton(moveLeft);
        moveLeft.addActionListener(
                e -> {
                    SimulationStateManager
                            .getInstance()
                            .getCurrentSimulation()
                            .removeUserStoryFromSprint(SimulationStateManager.getInstance().getCurrentSimulation().getSprintByString(
                                    (String) selectSprintComboBox.getSelectedItem()), selectedSprintUserStory);
                    redrawUserStoriesList(userStories);
                    redrawSprintUserStoriesList(selectSprintComboBox, sprintUserStories);
                });
        return moveLeft;
    }

    private JButton getMoveRight(JComboBox<String> selectSprintComboBox, JList<String> userStories,
            JList<String> sprintUserStories) {
        JButton moveRight = new JButton(">");
        styleButton(moveRight);
        moveRight.addActionListener(
                e -> {
                    SimulationStateManager
                            .getInstance()
                            .getCurrentSimulation()
                            .addUserStoryToSprint(SimulationStateManager.getInstance().getCurrentSimulation().getSprintByString(
                                    (String) selectSprintComboBox.getSelectedItem()), selectedUserStory);
                    redrawUserStoriesList(userStories);
                    redrawSprintUserStoriesList(selectSprintComboBox, sprintUserStories);
                });
        return moveRight;
    }

    private JButton getRandomizeButton(JComboBox<String> selectSprintComboBox, JList<String> userStories,
            JList<String> sprintUserStories) {
        JButton randomizeButton = new JButton("Randomize");
        randomizeButton.addActionListener(
                e -> {
                    int response = JOptionPane.showConfirmDialog(this,
                            "Are you sure you want to randomize the sprint backlog?", "Randomize Sprint Backlog",
                            JOptionPane.YES_NO_OPTION);

                    if (response != JOptionPane.YES_OPTION) {
                        return;
                    }

                    SimulationStateManager
                            .getInstance()
                            .getCurrentSimulation()
                            .randomizeSprintBacklog();
                    redrawUserStoriesList(userStories);
                    redrawSprintUserStoriesList(selectSprintComboBox, sprintUserStories);
                });
        return randomizeButton;
    }

    private void redrawUserStoriesList(JList<String> userStoriesComponent) {

        updateUserStories();

        String[] userStoryNames = this.userStories.stream()
                .filter(userStory -> userStory.getStatus().equals(UserStory.UserStoryStatus.UNSELECTED))
                .map(Objects::toString).toList().toArray(new String[] {});

        userStoriesComponent.setVisibleRowCount(20);

        userStoriesComponent.addListSelectionListener(
                e -> selectedUserStory = userStoriesComponent.getSelectedValue());

        userStoriesComponent.setListData(userStoryNames);
    }

    private void redrawSprintUserStoriesList(JComboBox<String> selectSprintComboBox,
            JList<String> userStoriesComponent) {
        String[] userStories = SimulationStateManager.getInstance().getCurrentSimulation()
                .getSprintByString((String) selectSprintComboBox.getSelectedItem())
                .getUserStories()
                .stream()
                .filter(userStory -> userStory.getStatus().equals(UserStory.UserStoryStatus.ADDED))
                .map(Objects::toString)
                .toList()
                .toArray(new String[] {});
        userStoriesComponent.setVisibleRowCount(20);

        userStoriesComponent.addListSelectionListener(
                e -> selectedSprintUserStory = userStoriesComponent.getSelectedValue());
        userStoriesComponent.setListData(userStories);
    }
     private JButton CancelButton() {
                JButton cancelButton = new JButton("Close");
                cancelButton.addActionListener(e -> closeWindow());
                return cancelButton;
        }

        private void closeWindow() {
                dispose();
        }
        private void styleButton(JButton button) {
                button.setBackground(new Color(70, 130, 180));
                button.setForeground(Color.WHITE);
                button.setFont(new Font("Arial", Font.BOLD, 20));
}
    @Override
    public void setVisible(boolean b) {
        if (SimulationStateManager.getInstance().getCurrentSimulation() == null) {
            return;
        }
        super.setVisible(b);
    }

}
