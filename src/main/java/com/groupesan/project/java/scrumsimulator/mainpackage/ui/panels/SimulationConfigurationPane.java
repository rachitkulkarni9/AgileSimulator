package com.groupesan.project.java.scrumsimulator.mainpackage.ui.panels;

import com.groupesan.project.java.scrumsimulator.mainpackage.core.Simulation;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationSingleton;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager;
import com.groupesan.project.java.scrumsimulator.mainpackage.ui.widgets.BaseComponent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;

public class SimulationConfigurationPane extends JFrame implements BaseComponent {
    private DefaultTableModel tableModel;
    private JTable simulationsTable;
    private final JFrame parent;
    private JPanel myJpanel;

    public SimulationConfigurationPane(JFrame parent) {
        this.parent = parent;
        this.init();
        setupGlassPane();
    }

    @Override
    public void init() {
        setSize(800, 600);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle("All Simulations List");

        myJpanel = new JPanel();
        myJpanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        myJpanel.setLayout(new BorderLayout());

        myJpanel.add(new JLabel("Double click on any simulation below to set as the current simulation context"), BorderLayout.NORTH);

        JButton addSimulationButton = initializeAddSimulationButton();
        myJpanel.add(addSimulationButton, BorderLayout.SOUTH);

        String[] columnNames = {"ID", "Name", "Random Seed", "Sprint Count", "Sprint Duration (Days)", "# of User Stories", "Active",
                "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7;
            }
        };

        simulationsTable = new JTable(tableModel);
        simulationsTable.getColumn("Actions").setCellRenderer(new SimulationConfigurationPane.ButtonRenderer());
        simulationsTable.getColumn("Actions").setCellEditor(new SimulationConfigurationPane.ButtonEditor(new JCheckBox()));
        refreshTableData();

        simulationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1
                        && simulationsTable.getSelectedRow() != -1) {
                    int row = simulationsTable.getSelectedRow();
                    if (row != -1) {
                        Object id = simulationsTable.getValueAt(row, 0);
                        Simulation simulation = SimulationSingleton.getInstance().getSimulationById((UUID) id);
                        SimulationStateManager.getInstance().setCurrentSimulation(simulation);
                        refreshTableData();
                        JOptionPane.showMessageDialog(myJpanel, "Simulation: '%s' loaded as current context".formatted(simulationsTable.getValueAt(row, 1)));
                    }
                }
            }
        });

        simulationsTable.setFillsViewportHeight(true);
        simulationsTable.setAutoCreateRowSorter(true);
        simulationsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(simulationsTable);

        myJpanel.add(scrollPane, BorderLayout.CENTER);
        add(myJpanel);
    }

    private JButton initializeAddSimulationButton() {
        JButton addSimulationButton = new JButton("Add Simulation");
        addSimulationButton.addActionListener(e -> {
            ModifySimulationPane newSimulationPane = new ModifySimulationPane(this);
            newSimulationPane.setVisible(true);
            newSimulationPane.setAlwaysOnTop(true);
            newSimulationPane.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    refreshTableData();
                }
            });
        });
        return addSimulationButton;
    }

    private void openEditForm(Simulation simulation) {
        ModifySimulationPane form = new ModifySimulationPane(this, simulation);
        this.setAlwaysOnTop(false);
        form.setAlwaysOnTop(true);
        setGlassPaneVisible(true);
        form.setVisible(true);
        form.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                refreshTableData();
                setGlassPaneVisible(false);
                setAlwaysOnTop(true);
            }
        });
    }

    private void refreshTableData() {
        tableModel.setRowCount(0);
        SimulationSingleton.getInstance().getAllSimulations().forEach(simulation -> {
            Object[] rowData = {
                    simulation.getSimulationId(),
                    simulation.getName(),
                    simulation.getRandomSeed(),
                    simulation.getSprintCount(),
                    simulation.getSprintDuration(),
                    simulation.getUserStories().size(),
                    SimulationStateManager.getInstance().getCurrentSimulation().equals(simulation),
                    "Actions"
            };
            tableModel.addRow(rowData);
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
                Simulation simulation = SimulationSingleton.getInstance().getSimulationById(
                        (UUID) tableModel.getValueAt(simulationsTable.getSelectedRow(), 0)
                );
                JPopupMenu popupMenu = new JPopupMenu();
                JMenuItem setContextItem = new JMenuItem("Set As Current Context");
                JMenuItem editItem = new JMenuItem("Edit");
                JMenuItem deleteItem = new JMenuItem("Delete");
                JMenuItem duplicateItem = new JMenuItem("Duplicate");

                setContextItem.addActionListener(e -> {
                    SimulationStateManager.getInstance().setCurrentSimulation(simulation);
                    JOptionPane.showMessageDialog(myJpanel, "Simulation: '%s' loaded as current context"
                            .formatted(simulationsTable.getValueAt(simulationsTable.getSelectedRow(), 1)));
                });
                editItem.addActionListener(e -> openEditForm(simulation));
                deleteItem.addActionListener(e -> {
                    SimulationSingleton.getInstance().removeSimulation(simulation);
                    SimulationStateManager.getInstance()
                            .setCurrentSimulation(SimulationSingleton.getInstance().getLatestSimulation());
                    refreshTableData();
                });
                duplicateItem.addActionListener(e -> {
                    SimulationSingleton.getInstance().addSimulation(simulation.deepClone());
                    refreshTableData();
                });

                popupMenu.add(setContextItem);
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
