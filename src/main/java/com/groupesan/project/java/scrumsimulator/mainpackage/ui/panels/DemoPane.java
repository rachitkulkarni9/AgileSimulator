package com.groupesan.project.java.scrumsimulator.mainpackage.ui.panels;

import com.groupesan.project.java.scrumsimulator.mainpackage.core.*;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationSingleton;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager;
import com.groupesan.project.java.scrumsimulator.mainpackage.ui.widgets.BaseComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.border.EmptyBorder;

public class DemoPane extends JFrame implements BaseComponent {
    private JPanel myJpanel;
    private JButton userStoriesButton, startSimulationButton, potentialBlockersButton,
            sprintBacklogsButton, simulationConfigButton, potentialBlockerSolutionsButton;

    private JPanel bottomPanel;

    public DemoPane() {
        this.init();
    }

    public void init() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Scrum Simulator");
        setSize(1000, 700);
        setLayout(new BorderLayout(10, 10));

        GridBagLayout myGridbagLayout = new GridBagLayout();
        myJpanel = new JPanel();
        myJpanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        myJpanel.setLayout(myGridbagLayout);

        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        bottomPanel = new JPanel(new BorderLayout(10, 10));
        redrawUIBasedOnRole();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                SimulationSingleton.getInstance().saveSimulationDetails();
            }
        });
    }

    public void redrawUIBasedOnRole() {
        UserRole role = UserRoleSingleton.getInstance().getUserRole();
        bottomPanel.removeAll();
        repaint();
        setupButtons();

        JPanel centerPanel = createCenterPanel(role);
        bottomPanel.add(centerPanel, BorderLayout.CENTER);

        JPanel rightPanel = createRightPanel(role);
        bottomPanel.add(rightPanel, BorderLayout.EAST);

        setupGlassPane();
        add(bottomPanel);
        StylePane.applyStyle(this);
    }

    private void setupButtons() {
        simulationConfigButton = new JButton("Simulation Configuration");
        simulationConfigButton.addActionListener(
                e -> handleButtonAction(new SimulationConfigurationPane(this)));

        userStoriesButton = new JButton("Product Backlog (User Stories)");
        userStoriesButton.addActionListener(
                e -> handleButtonAction(new UserStoriesPane(this)));

        startSimulationButton = new JButton("Start Simulation");
        startSimulationButton.addActionListener(
                e -> onStartSimulationClick());

        potentialBlockersButton = new JButton("Potential Blockers");
        potentialBlockersButton.addActionListener(
                e -> handleButtonAction(new PotentialBlockersPane(this)));

        potentialBlockerSolutionsButton = new JButton("Potential Blocker Solutions");
        potentialBlockerSolutionsButton.addActionListener(
                e -> handleButtonAction(new PotentialBlockerSolutionsPane(this)));

        sprintBacklogsButton = new JButton("Assign Sprint Backlogs");
        sprintBacklogsButton.addActionListener(
                e -> handleButtonAction(new SprintBacklogPane(this)));

        new DemoPaneBuilder(myJpanel)
                .addComponent(simulationConfigButton, 0, 0)
                .addComponent(userStoriesButton, 1, 0)
                .addComponent(startSimulationButton, 2, 0)
                .addComponent(potentialBlockersButton, 3, 0)
                .addComponent(potentialBlockerSolutionsButton, 4, 0)
                .addComponent(sprintBacklogsButton, 5, 0)
                .buildPanel();

        add(myJpanel);
    }

    private JComboBox<String> roleComboBox;

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel roleLabel = new JLabel("Current Role:");
        roleComboBox = new JComboBox<>(new String[]{"Scrum Administrator", "Scrum Master", "Developer", "Product Owner"});
        roleComboBox.setPreferredSize(new Dimension(150, 25));
        UserRoleSingleton.getInstance().setUserRole(UserRole.SCRUM_ADMIN);
        roleComboBox.addActionListener(e -> {
            String selectedRole = (String) roleComboBox.getSelectedItem();
            UserRole role = UserRoleSingleton.getUserRoleValueFromLabel(selectedRole);
            UserRoleSingleton.getInstance().setUserRole(role);
            redrawUIBasedOnRole();
        });

        panel.add(roleLabel);
        panel.add(roleComboBox);
        return panel;
    }

    public void updateRoleSelection(String selectedRole) {
        roleComboBox.setSelectedItem(selectedRole);
    }

    private JPanel createCenterPanel(UserRole role) {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Main Actions"));

        switch (role) {
            case SCRUM_MASTER:
                panel.add(createButton("Assign Sprint Backlogs", this::onAssignSprintBacklogClick));
                break;
            case DEVELOPER:
                panel.add(createButton("Product Backlog (User Stories)", this::onUserStoriesClick));
                break;
            case PRODUCT_OWNER:
                panel.add(createButton("Product Backlog (User Stories)", this::onUserStoriesClick));
                break;
            case SCRUM_ADMIN:
                panel.add(createButton("Assign Sprint Backlogs", this::onAssignSprintBacklogClick));
                panel.add(createButton("Product Backlog (User Stories)", this::onUserStoriesClick));
                panel.add(createButton("Potential Blockers", this::onPotentialBlockersClick));
                panel.add(createButton("Potential Blocker Solutions", this::onPotentialSolutionsClick));
                break;
        }
        return panel;
    }


    private JPanel createRightPanel(UserRole role) {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Simulation Controls"));

        // Switch case syntax is not ideal, but it's easier than fixing Checkstyle
        // plugin at this time
        switch (role) {
            case SCRUM_MASTER:
                panel.add(createButton("Simulation Configuration", () -> handleButtonAction(new SimulationConfigurationPane(this))));
                panel.add(createButton("Start Simulation", this::onStartSimulationClick));
                break;
            case DEVELOPER:
                break;
            case PRODUCT_OWNER:
                break;
            case SCRUM_ADMIN:
                panel.add(createButton("Simulation Configuration", () -> handleButtonAction(new SimulationConfigurationPane(this))));
                panel.add(createButton("Start Simulation", this::onStartSimulationClick));
                break;
        }
        return panel;
    }

    private JButton createButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.addActionListener(e -> action.run());
        return button;
    }

    private void handleButtonAction(JFrame pane) {
        setMenuButtonsEnabled(false);
        setGlassPaneVisible(true);
        pane.setVisible(true);
        pane.setAlwaysOnTop(true);

        pane.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                setMenuButtonsEnabled(true);
                setGlassPaneVisible(false);
            }
        });
    }

    private void setupGlassPane() {
        JPanel glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        glassPane.addMouseListener(new java.awt.event.MouseAdapter() {
        });
        glassPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
        });

        glassPane.setOpaque(false);
        setGlassPane(glassPane);
    }

    private void setGlassPaneVisible(boolean visible) {
        getGlassPane().setVisible(visible);
    }

    private void setMenuButtonsEnabled(boolean enabled) {
        simulationConfigButton.setEnabled(enabled);
        userStoriesButton.setEnabled(enabled);
        startSimulationButton.setEnabled(enabled);
        potentialBlockersButton.setEnabled(enabled);
        potentialBlockerSolutionsButton.setEnabled(enabled);
        sprintBacklogsButton.setEnabled(enabled);
    }

    private boolean contextHasActiveSimulation() {
        if (SimulationStateManager.getInstance().getCurrentSimulation() == null) {
            JOptionPane.showMessageDialog(this, "Please Select a Simulation Configuration before continuing");
            return false;
        }
        return true;
    }

    private void onAssignSprintBacklogClick() {
        if (contextHasActiveSimulation()) {
            handleButtonAction(new SprintBacklogPane(this));
        }
    }

    private void onPotentialBlockersClick() {
        if (contextHasActiveSimulation()) {
            handleButtonAction(new PotentialBlockersPane(this));
        }
    }

    private void onPotentialSolutionsClick() {
        if (contextHasActiveSimulation()) {
            handleButtonAction(new PotentialBlockerSolutionsPane(this));
        }
    }

    private void onUserStoriesClick() {
        if (contextHasActiveSimulation()) {
            handleButtonAction(new UserStoriesPane(this));
        }
    }

    private void onStartSimulationClick() {
        if(contextHasActiveSimulation()) {
            handleButtonAction(new SimulationPane(this));
        }
    }
}
