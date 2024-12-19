package com.groupesan.project.java.scrumsimulator.mainpackage.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import com.groupesan.project.java.scrumsimulator.mainpackage.core.UserAction;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.UserRolePermissions;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.UserRoleSingleton;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationSingleton;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager;
import com.groupesan.project.java.scrumsimulator.mainpackage.ui.widgets.BaseComponent;

public class UserStoriesPane extends JFrame implements BaseComponent {
    private DefaultTableModel tableModel;
    private JTable userStoriesTable;
    private JPanel glassPane;
    private JFrame parent;

    public UserStoriesPane(JFrame parent) {
        this.parent = parent;
        this.init();
        setupGlassPane();
    }

    @Override
    public void init() {
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("User Story List");

        JPanel myJpanel = new JPanel();
        myJpanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        myJpanel.setLayout(new BorderLayout());

        myJpanel.add(new JLabel("Double click on any User Story below to edit it"), BorderLayout.NORTH);

        if (UserRolePermissions.actionAllowed(UserRoleSingleton.getInstance().getUserRole(),
                UserAction.MANAGE_USER_STORES)) {
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BorderLayout());

            JButton addUserStoryButton = new JButton("Add User Story");
            addUserStoryButton.addActionListener(handleNewUserStoryAction());
            buttonPanel.add(addUserStoryButton, BorderLayout.NORTH);

            JButton initializeUserStories = new JButton("Add Default User Stories");
            initializeUserStories.addActionListener(handleInitializeUserStoriesAction());
            buttonPanel.add(initializeUserStories, BorderLayout.SOUTH);

            myJpanel.add(buttonPanel, BorderLayout.SOUTH);
        }

        String[] columnNames = { "Name", "Story Points", "BV Points", "Description", "Actions" };
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        userStoriesTable = new JTable(tableModel);
        userStoriesTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        userStoriesTable.getColumn("Actions").setCellEditor(new ButtonEditor(new JCheckBox()));
        reloadUserStories();

        userStoriesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
                        && userStoriesTable.getSelectedRow() != -1) {
                    int row = userStoriesTable.getSelectedRow();
                    if (row != -1) {
                        UserStory userStory = SimulationStateManager.getInstance().getCurrentSimulation()
                                .getUserStories().get(row);
                        openEditForm(userStory);
                    }
                }
            }
        });

        userStoriesTable.setFillsViewportHeight(true);
        userStoriesTable.setAutoCreateRowSorter(true);
        userStoriesTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(userStoriesTable);

        myJpanel.add(scrollPane, BorderLayout.CENTER);
        add(myJpanel);
    }

    private ActionListener handleInitializeUserStoriesAction() {
        return e -> {
            SimulationSingleton.getInstance().initializeDefaultUserStories();
            reloadUserStories();
        };
    }

    private ActionListener handleNewUserStoryAction() {
        return e -> {
            openNewForm("", 0, 0, "");
            reloadUserStories();
        };
    }

    private void reloadUserStories() {
        tableModel.setRowCount(0);

        SimulationStateManager.getInstance().getCurrentSimulation().getUserStories().forEach(userStory -> {
            Object[] rowData = {
                    userStory.getName(),
                    userStory.getPointValue(),
                    userStory.getBusinessValuePoint(),
                    userStory.getDescription(),
                    "Actions"
            };
            tableModel.addRow(rowData);
        });

        userStoriesTable.revalidate();
        userStoriesTable.repaint();
    }

    private void openNewForm(String name, double storyPoints, int bvPoints, String description) {
        EditUserStoryForm form = new EditUserStoryForm(name, storyPoints, bvPoints, description);
        this.setAlwaysOnTop(false);
        form.setAlwaysOnTop(true);
        setGlassPaneVisible(true);
        form.setVisible(true);
        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                reloadUserStories();
                setGlassPaneVisible(false);
                setAlwaysOnTop(true);
            }
        });
    }

    private void openEditForm(UserStory userStory) {
        EditUserStoryForm form = new EditUserStoryForm(userStory);
        this.setAlwaysOnTop(false);
        form.setAlwaysOnTop(true);
        setGlassPaneVisible(true);
        form.setVisible(true);
        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                reloadUserStories();
                setGlassPaneVisible(false);
                setAlwaysOnTop(true);
            }
        });
    }

    private void setupGlassPane() {
        glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 100));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        glassPane.addMouseListener(new MouseAdapter() {
        });
        glassPane.addMouseMotionListener(new MouseAdapter() {
        });
        glassPane.setOpaque(false);
        setGlassPane(glassPane);
    }

    private void setGlassPaneVisible(boolean visible) {
        getGlassPane().setVisible(visible);
        getGlassPane().repaint();
    }

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Actions" : value.toString());
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Actions" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                UserStory userStory = SimulationStateManager.getInstance().getCurrentSimulation()
                        .getUserStories().get(userStoriesTable.getSelectedRow());
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem editItem = new JMenuItem("Edit");
                JMenuItem deleteItem = new JMenuItem("Delete");
                JMenuItem duplicateItem = new JMenuItem("Duplicate");

                editItem.addActionListener(e -> openEditForm(userStory));

                deleteItem.addActionListener(e -> {
                    SimulationStateManager.getInstance().getCurrentSimulation().removeUserStory(userStory);
                    reloadUserStories();
                });

                duplicateItem.addActionListener(e -> {
                    UserStory duplicate = new UserStory(userStory.getName() + " - Copy", userStory.getDescription(),
                            userStory.getPointValue(), userStory.getBusinessValuePoint());
                    SimulationStateManager.getInstance().getCurrentSimulation().addUserStory(duplicate);
                    reloadUserStories();
                });

                popupMenu.add(editItem);
                popupMenu.add(deleteItem);
                popupMenu.add(duplicateItem);
                popupMenu.show(button, button.getWidth(), button.getHeight());
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
}
