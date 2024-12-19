package com.groupesan.project.java.scrumsimulator.mainpackage.ui.dialogs.simulation;

import java.awt.event.ActionEvent;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerObject;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerType;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.Simulation;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.*;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager.SprintStateEnum;


public class SimulationProgressPane {
    private JPanel simPan;
    private JLabel jimPan;
    private JLabel currentProgressValue;
    private JProgressBar jimProg;
    private JButton pauseSimulationButton;
    private JScrollPane userStoryScrollPane;
    private  DefaultTableModel model = null;
    private JTable userStoryContainer;
    private JLabel messageLabel;
    private BurndownChart burndownChart;
    private Double totalPoints;
    private int currentDay;



    public SimulationProgressPane() {
        simPan = new JPanel();
        simPan.setLayout(new BoxLayout(simPan, BoxLayout.Y_AXIS));
        simPan.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        jimPan = new JLabel();
        currentProgressValue = new JLabel();
        messageLabel = new JLabel();
        jimProg = new JProgressBar(0, 100);
        currentDay = 0;


        burndownChart = new BurndownChart();
        pauseSimulationButton = new JButton("Pause Simulation");
        pauseSimulationButton.addActionListener(this::handlePauseSimulation);
        totalPoints = calculateTotal();
        burndownChart.setBurndown(0, totalPoints);
        burndownChart.updateChart();





        String[] userStoryColumnNames = { "User Story Name", "Status", "UUID", "Set In Progress", "Set Ready For Test", "Set Blocked" , "Set Spiked", "Set Completed"};
        model = new DefaultTableModel(userStoryColumnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3;
            }
        };
        userStoryContainer = new JTable(model);


        userStoryScrollPane = new JScrollPane(userStoryContainer);
        for(int i = 3; i < userStoryColumnNames.length; i++) {
            userStoryContainer.getColumn(userStoryColumnNames[i]).setCellRenderer(new ButtonRenderer());
            userStoryContainer.getColumn(userStoryColumnNames[i]).setCellEditor(new ButtonEditor(new JCheckBox(), model, this));
        }
        // userStoryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);



        simPan.add(jimPan);
        simPan.add(currentProgressValue);
        simPan.add(jimProg);
        simPan.add(messageLabel);
        simPan.add(pauseSimulationButton);
        simPan.add(userStoryScrollPane);
        burndownChart.setVisible(true);
        simPan.add(burndownChart);

    }

    private double calculateTotal() {
        double num = 0.0;
        SimulationStateManager stateManager = SimulationStateManager.getInstance();
        Simulation currentSimulation = stateManager.getCurrentSimulation();
        int currentSprint = stateManager.getSprintNum();
        for(UserStory userStory : currentSimulation.getSprints().get(currentSprint-1).getUserStories()){
            num += userStory.getPointValue();
        }
        return num;
    }

    private void setLinearVelocity(int totalDays) {
        for(int i = 0; i <= totalDays; i++) {
            double points = calculateTotal() * (1 - (double)i / totalDays);
            burndownChart.setLinearLine(i, points);
        }
    }

    public void setMessage(String text) {
        messageLabel.setText(text);
    }

    public void addUserStory(UserStory USText) {
        model.addRow(new Object[] { USText.getName(), "Added", USText.getId(), "In Progress", "Ready for test", "Blocked" , "Spiked", "Completed"});
        userStoryContainer.revalidate();
        userStoryContainer.repaint();
    }


    public void setChart(Integer day, Double points) {
        totalPoints-= points;
        burndownChart.setBurndown(day, totalPoints);
        burndownChart.updateChart();

        userStoryContainer.revalidate();
        userStoryContainer.repaint();
    }

    public void changeState(UserStory userStory) {
        UserStoryState userStoryState = userStory.getUserStoryState();
        if(userStoryState instanceof UserStoryInProgressState) {
                setStatus(userStory, "In Progress");
        }
        else if(userStoryState instanceof UserStoryBlockedState) {
            int recentBlocker = userStory.getBlockers().size();
            String blocker = String.valueOf(userStory.getBlockers().get(recentBlocker-1));
            setStatus(userStory, "Blocked - " + blocker.substring(20));
        }
        else if(userStoryState instanceof UserStoryTestState) {
            setStatus(userStory, "Ready for test");
        }
        else if(userStoryState instanceof UserStorySpikedState) {
            setStatus(userStory, "SPIKED");
        }
        else if(userStoryState instanceof UserStoryCompletedState) {

            setStatus(userStory, "Completed");
        }
        userStoryContainer.revalidate();
        userStoryContainer.repaint();
    }

    /**
     * Set the status of the stories colour depending on status
     * Citation of source that helped:
     * https://stackoverflow.com/questions/14425364/swing-setting-the-color-of-a-cell-based-on-the-value-of-a-cell
     */
    public void inProgressState() {
        userStoryContainer.getColumn("Status").setCellRenderer(
                new DefaultTableCellRenderer() {
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object progress, boolean isSelected, boolean hasFocus, int row, int column) {
                        Component userStoryCell = super.getTableCellRendererComponent(table, progress, isSelected, hasFocus, row, column);

                        if ("Added".equals(progress)) {
                            userStoryCell.setForeground(Color.ORANGE);
                        }
                        else if ("In Progress".equals(progress)) {
                            userStoryCell.setForeground(Color.BLUE);
                        }
                        else if("Ready for test".equals(progress)) {
                            userStoryCell.setForeground(Color.CYAN);
                        }
                        else if (progress.toString().contains("Blocked")) {
                            userStoryCell.setForeground(Color.RED);
                        }
                        else if ("SPIKED".equals(progress)) {
                            userStoryCell.setForeground(Color.MAGENTA);
                        }
                        else if ("Completed".equals(progress)) {
                            userStoryCell.setForeground(Color.GREEN);
                        } else {
                            userStoryCell.setForeground(Color.BLACK);
                        }

                        return userStoryCell;
                    }
                }
         );
        //userStoryContainer.revalidate();
        userStoryContainer.repaint();
    }



    public int getCurrentDay() {
        return currentDay;
    }

    public void resetPanel() {
        // Had to remove SwingUtilities to be able to refresh the panel.

        burndownChart.resetData();
        totalPoints = calculateTotal();
//        SimulationStateManager stateManager = SimulationStateManager.getInstance();
//        Simulation currentSimulation = stateManager.getCurrentSimulation();
//        int currentSprint = stateManager.getSprintNum();
//        int y = currentSimulation.getSprints().get(currentSprint-1).getLength();
        burndownChart.setBurndown(0, totalPoints);
        burndownChart.updateChart();
        for(int i = model.getRowCount()-1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    private void setStatus(UserStory US, String status) {

        int rowCount = model.getRowCount();
        int userStoryRow = 2;
        int statusColumn = 1;
        UUID selectedUS = US.getId();


        for(int i = 0; i < rowCount; i++){
            UUID currentUS = (UUID) model.getValueAt(i, userStoryRow);
            if(currentUS.equals(selectedUS)) {
                model.setValueAt(status, i, statusColumn);
                break;
            }
        }

    }

    private void handlePauseSimulation(ActionEvent e) {
        SimulationStateManager stateManager = SimulationStateManager.getInstance();
        SprintStateEnum state = stateManager.getState();

        if (state == SprintStateEnum.RUNNING) {
            stateManager.setState(SprintStateEnum.PAUSED);
            pauseSimulationButton.setText("Start Simulation");

        } else if (state == SprintStateEnum.PAUSED) {
            stateManager.setState(SprintStateEnum.RUNNING);
            pauseSimulationButton.setText("Pause Simulation");

        }

        userStoryContainer.revalidate();
        userStoryContainer.repaint();

    }

    public JPanel getSimPan() {
        return simPan;
    }

    public void updateProgress(int progressValue, int day, int sprint, int sprintDuration) {
        SwingUtilities.invokeLater(() -> {
            try {
                if(day < sprintDuration)
                setLinearVelocity(sprintDuration);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            currentDay = day;
            jimPan.setText("Running simulation for day " + day + " of " + sprintDuration + " of sprint " + sprint);
            currentProgressValue.setText("Progress: " + progressValue + "%");
            jimProg.setValue(progressValue);
        });
    }

    public void resetProgress() {
        SwingUtilities.invokeLater(() -> {
            currentProgressValue.setText("Progress: 0%");
            jimProg.setValue(0);
        });
    }


    static class ButtonRenderer extends JButton implements TableCellRenderer {


        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Actions" : value.toString());
            SimulationStateManager stateManager = SimulationStateManager.getInstance();
            SprintStateEnum state = stateManager.getState();
            boolean bool = state != SprintStateEnum.RUNNING;
            setEnabled(bool);

            if(state == SprintStateEnum.RUNNING || state == SprintStateEnum.STOPPED) {
                setBackground(Color.LIGHT_GRAY);
            } else {
                setBackground(null);
            }

            return this;
        }

    }

    /**
     * Borrowing elements of PotentialBlockerSolutionsPane
     */
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private DefaultTableModel tabModel;
        private SimulationProgressPane simPane;
        private int column;
        private int row;
        public ButtonEditor(JCheckBox checkBox, DefaultTableModel model, SimulationProgressPane sim) {
            super(checkBox);
            button = new JButton();
            tabModel = model;
            this.simPane = sim;
            button.setOpaque(true);
            button.addActionListener(e -> {
                if (button.isEnabled()) {
                    fireEditingStopped();
                }
            });
            //button.setVisible(false);
        }


        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            SimulationStateManager stateManager = SimulationStateManager.getInstance();
            SprintStateEnum state = stateManager.getState();
            label = (value == null) ? "Actions" : value.toString();

            this.table = table;
            this.row = row;
            this.column = column;
            button.setEnabled(state != SprintStateEnum.RUNNING);

            if(state == SprintStateEnum.RUNNING || state == SprintStateEnum.STOPPED) {
                button.setBackground(Color.LIGHT_GRAY);
                return null;
            }

            isPushed = true;
            return button;
        }




        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        public boolean stateChecker(UserStoryState userStoryState, String buttonValue) {


            switch (buttonValue) {
                case "In Progress":
                    if(userStoryState instanceof UserStoryCompletedState
                            || (userStoryState instanceof UserStorySpikedState)
                            || (userStoryState instanceof UserStoryTestState)) {
                        return false;
                    }
                    break;
                case "Blocked":
                    if((userStoryState instanceof UserStorySpikedState)
                            || (userStoryState instanceof UserStoryNewState)
                            || (userStoryState instanceof UserStoryCompletedState)) {
                        return false;
                    }
                    break;
                case "Spiked":
                    if(!(userStoryState instanceof UserStoryBlockedState)) {
                        return false;
                    }
                    break;
                case "Ready for test":
                    if((userStoryState instanceof UserStoryNewState) || (userStoryState instanceof UserStoryCompletedState)) {
                        return false;
                    }
                    break;
                case "Completed":
                    if(!(userStoryState instanceof UserStoryTestState)) {
                        return false;
                    }
                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        protected void fireEditingStopped() {
            if(isPushed) {
                super.fireEditingStopped();
                Object c1 = tabModel.getValueAt(row, 2);
                Object c3 = tabModel.getValueAt(row, column);



                isPushed = false;

                SimulationStateManager stateManager = SimulationStateManager.getInstance();
                Simulation currentSimulation = stateManager.getCurrentSimulation();
                int currentSprint = stateManager.getSprintNum();
                for (UserStory userStory : currentSimulation.getSprints().get(currentSprint-1).getUserStories()) {
                    if(userStory.getId().equals(c1)) {
                        switch (c3.toString()) {
                            case "In Progress":
                                if(!stateChecker(userStory.getUserStoryState(), c3.toString())) {

                                    simPane.setMessage("User Story can be changed to In Progress if it is Blocked or Spiked");
                                    break;

                                } else {
                                    if(userStory.isBlocked()) {
                                        userStory.resolveBlockers();
                                    }
                                    userStory.changeState(new UserStoryInProgressState(userStory));
                                    tabModel.setValueAt("In Progress", row, 1);
                                }
                                SimulationProgressPane.this.setMessage("");
                                break;
                            case "Ready for test":
                                if(!stateChecker(userStory.getUserStoryState(), c3.toString())) {

                                    SimulationProgressPane.this.setMessage("Ready for Test can be activated from In Progress, Blocked, or Spiked");
                                    break;

                                } else {
                                    if(userStory.isBlocked()) {
                                        userStory.resolveBlockers();
                                    }
                                    userStory.changeState(new UserStoryTestState(userStory));
                                    tabModel.setValueAt("Ready for test", row, 1);
                                }
                                SimulationProgressPane.this.setMessage("");
                                break;
                            case "Completed":
                                if(!stateChecker(userStory.getUserStoryState(), c3.toString())) {

                                    SimulationProgressPane.this.setMessage("User story must be Ready for Test before it can be completed");
                                    break;

                                }
                                if(userStory.isBlocked()) {
                                    userStory.resolveBlockers();
                                }
                                userStory.changeState(new UserStoryCompletedState(userStory));
                                tabModel.setValueAt("Completed", row, 1);
                                simPane.setChart(simPane.currentDay, userStory.getPointValue());
                                SimulationProgressPane.this.setMessage("");
                                break;
                            case "Blocked":
                                if(!stateChecker(userStory.getUserStoryState(), c3.toString())) {

                                    SimulationProgressPane.this.setMessage("User Story can only be Blocked if it is In Progress or Ready for Test");
                                    break;

                                }
                                userStory.changeState(new UserStoryBlockedState(userStory));
                                BlockerType blockerTypeManual = new BlockerType("Manual", 0, 90, 10);
                                BlockerObject blockerManual = new BlockerObject(blockerTypeManual);
                                userStory.setBlocker(blockerManual);
                                tabModel.setValueAt("Blocked - Manually", row, 1);
                                SimulationProgressPane.this.setMessage("");
                                break;
                            case "Spiked":
                                if(!stateChecker(userStory.getUserStoryState(), c3.toString())) {

                                    SimulationProgressPane.this.setMessage("User Story can only be spiked if it is blocked");
                                    break;

                                }
                                userStory.changeState(new UserStorySpikedState(userStory));
                                tabModel.setValueAt("SPIKED", row, 1);
                                SimulationProgressPane.this.setMessage("");
                                break;
                            default:
                                break;
                        }
                    }
                }


            }
        }
    }
}

