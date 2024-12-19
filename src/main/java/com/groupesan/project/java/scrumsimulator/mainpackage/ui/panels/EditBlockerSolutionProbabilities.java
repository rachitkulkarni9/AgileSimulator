package com.groupesan.project.java.scrumsimulator.mainpackage.ui.panels;

import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerSolution;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager;
import com.groupesan.project.java.scrumsimulator.mainpackage.ui.widgets.BaseComponent;
import com.groupesan.project.java.scrumsimulator.mainpackage.utils.CustomConstraints;
import com.groupesan.project.java.scrumsimulator.mainpackage.utils.RandomUtils;

import javax.swing.*;
import java.awt.*;

public class EditBlockerSolutionProbabilities extends JFrame implements BaseComponent {

    private final String solutionName;
    private final int probabilityChance;
    private boolean randomMode;

    private JLabel nameLabel;
    private JLabel probabilityLabel;
    private JLabel lowerBoundProbabilityValueLabel;
    private JLabel upperBoundProbabilityValueLabel;

    private JPanel myPanel;
    private JCheckBox randomModeCheckBox;
    private JTextField nameField;
    private JTextField probabilityChanceField;
    private JSlider lowerBoundRandomProbabilitySlider;
    private JSlider upperBoundRandomProbabilitySlider;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton deleteButton;

    public EditBlockerSolutionProbabilities(String solutionName, int probabilityChance) {
        this.solutionName = solutionName;
        this.probabilityChance = probabilityChance;
        this.randomMode = false;
        this.init();
    }

    @Override
    public void init() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("Edit Solution Probability");
        setSize(400, 300);

        myPanel = new JPanel();
        myPanel.setLayout(new GridBagLayout());

        nameLabel = new JLabel("Solution Name:");
        probabilityLabel = new JLabel("Chance (weight):");

        randomModeCheckBox = new JCheckBox("Random Mode");
        randomModeCheckBox.setName("randomModeCheckBox");

        nameField = new JTextField(solutionName, 20);
        nameField.setName("nameField");
        probabilityChanceField = new JTextField(String.valueOf(probabilityChance), 5);
        probabilityChanceField.setName("probabilityChanceField");

        lowerBoundRandomProbabilitySlider = new JSlider(0, 100, 0);
        upperBoundRandomProbabilitySlider = new JSlider(0, 100, 100);

        lowerBoundProbabilityValueLabel = new JLabel(String.valueOf(lowerBoundRandomProbabilitySlider.getValue()));
        upperBoundProbabilityValueLabel = new JLabel(String.valueOf(upperBoundRandomProbabilitySlider.getValue()));

        lowerBoundRandomProbabilitySlider.addChangeListener(e -> {
            if (lowerBoundRandomProbabilitySlider.getValue() > upperBoundRandomProbabilitySlider.getValue()) {
                lowerBoundRandomProbabilitySlider.setValue(upperBoundRandomProbabilitySlider.getValue());
            }
            lowerBoundProbabilityValueLabel.setText(String.valueOf(lowerBoundRandomProbabilitySlider.getValue()));
            probabilityChanceField.setText(String.valueOf(lowerBoundRandomProbabilitySlider.getValue()));
        });

        upperBoundRandomProbabilitySlider.addChangeListener(e -> {
            if (upperBoundRandomProbabilitySlider.getValue() < lowerBoundRandomProbabilitySlider.getValue()) {
                upperBoundRandomProbabilitySlider.setValue(lowerBoundRandomProbabilitySlider.getValue());
            }
            upperBoundProbabilityValueLabel.setText(String.valueOf(upperBoundRandomProbabilitySlider.getValue()));
            probabilityChanceField.setText(String.valueOf(upperBoundRandomProbabilitySlider.getValue()));
        });

        this.saveButton = getSaveButton(probabilityChanceField, nameField);
        saveButton.setName("saveButton");

        this.cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        cancelButton.setName("cancelButton");

        this.deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            BlockerSolution solution = SimulationStateManager.getInstance().getCurrentSimulation().getBlockerSolution(solutionName);
            if (solution != null) {
                SimulationStateManager.getInstance().getCurrentSimulation().removeBlockerSolution(solution);
            }
            dispose();
        });
        deleteButton.setName("deleteButton");
        deleteButton.setForeground(Color.RED);

        randomModeCheckBox.addItemListener(e -> {
            randomMode = randomModeCheckBox.isSelected();
            rebuildPanel();
        });

        rebuildPanel();
        add(myPanel);
    }

    private void rebuildPanel() {
        myPanel.removeAll();

        myPanel.add(nameLabel,
                new CustomConstraints(0, 0, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));
        myPanel.add(nameField,
                new CustomConstraints(1, 0, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));

        myPanel.add(probabilityLabel,
                new CustomConstraints(0, 1, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));

        myPanel.add(randomModeCheckBox,
                new CustomConstraints(0, 3, GridBagConstraints.WEST, 0.5, 1.0, GridBagConstraints.HORIZONTAL));
        myPanel.add(saveButton,
                new CustomConstraints(0, 4, GridBagConstraints.WEST, 0.5, 1.0, GridBagConstraints.HORIZONTAL));
        myPanel.add(cancelButton,
                new CustomConstraints(1, 4, GridBagConstraints.WEST, 0.5, 1.0, GridBagConstraints.HORIZONTAL));
        myPanel.add(deleteButton,
                new CustomConstraints(2, 4, GridBagConstraints.WEST, 0.5, 1.0, GridBagConstraints.HORIZONTAL));

        if (randomMode) {
            JPanel probabilityPanel = new JPanel();
            probabilityPanel.setLayout(new GridBagLayout());

            probabilityPanel.add(new JLabel("Lower Bound:"),
                    new CustomConstraints(0, 0, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));
            probabilityPanel.add(lowerBoundRandomProbabilitySlider,
                    new CustomConstraints(1, 0, GridBagConstraints.WEST, 2.0, 1.0, GridBagConstraints.HORIZONTAL));
            probabilityPanel.add(lowerBoundProbabilityValueLabel,
                    new CustomConstraints(2, 0, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));

            probabilityPanel.add(new JLabel("Upper Bound:"),
                    new CustomConstraints(0, 1, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));
            probabilityPanel.add(upperBoundRandomProbabilitySlider,
                    new CustomConstraints(1, 1, GridBagConstraints.WEST, 2.0, 1.0, GridBagConstraints.HORIZONTAL));
            probabilityPanel.add(upperBoundProbabilityValueLabel,
                    new CustomConstraints(2, 1, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));

            myPanel.add(probabilityPanel,
                    new CustomConstraints(1, 1, GridBagConstraints.WEST, 2.0, 1.0, GridBagConstraints.HORIZONTAL));
        } else {
            myPanel.add(probabilityChanceField,
                    new CustomConstraints(1, 1, GridBagConstraints.WEST, 1.0, 1.0, GridBagConstraints.HORIZONTAL));
        }

        myPanel.revalidate();
        myPanel.repaint();
    }

    private JButton getSaveButton(JTextField probabilityChanceField, JTextField nameField) {
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> {
            int finalProbability;

            if (randomMode) {
                finalProbability = RandomUtils.getInstance().getRandomInt(
                        lowerBoundRandomProbabilitySlider.getValue(),
                        upperBoundRandomProbabilitySlider.getValue());
            } else {
                finalProbability = Integer.parseInt(probabilityChanceField.getText());
            }

            BlockerSolution solution = SimulationStateManager.getInstance().getCurrentSimulation().getBlockerSolution(solutionName);
            if (solution == null) {
                solution = new BlockerSolution(nameField.getText(), finalProbability);
                SimulationStateManager.getInstance().getCurrentSimulation().addBlockerSolution(solution);
            } else {
                solution.setName(nameField.getText());
                solution.setChance(finalProbability);
            }
            dispose();
        });
        return saveButton;
    }
}
