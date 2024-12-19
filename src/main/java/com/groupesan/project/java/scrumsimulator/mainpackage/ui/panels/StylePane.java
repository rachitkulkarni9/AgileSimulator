package com.groupesan.project.java.scrumsimulator.mainpackage.ui.panels;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class StylePane {

    interface StyleStrategy {
        void applyStyle(Component component);
    }

    static class ButtonStyle implements StyleStrategy {
        public void applyStyle(Component component) {
            JButton button = (JButton) component;
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 20));
        }
    }

    static class PanelStyle implements StyleStrategy {
        public void applyStyle(Component component) {
            JPanel panel = (JPanel) component;
            panel.setBackground(new Color(240, 240, 240));
        }
    }

    static class TextStyle implements StyleStrategy {
        public void applyStyle(Component component) {
            JTextComponent textComp = (JTextComponent) component;
            textComp.setBackground(Color.WHITE);
            textComp.setForeground(new Color(50, 50, 50));
            textComp.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        }
    }

    static class ComboBoxStyle implements StyleStrategy {
        public void applyStyle(Component component) {
            JComboBox<?> comboBox = (JComboBox<?>) component;
            comboBox.setBackground(Color.WHITE);
            comboBox.setForeground(new Color(50, 50, 50));
            comboBox.setPreferredSize(new Dimension(220, comboBox.getPreferredSize().height));
        }
    }

    private static final Map<Class<?>, StyleStrategy> strategies = new HashMap<>();
    private static final Font DEFAULT_FONT = new Font("Arial", Font.PLAIN, 20);

    static {
        strategies.put(JButton.class, new ButtonStyle());
        strategies.put(JPanel.class, new PanelStyle());
        strategies.put(JTextField.class, new TextStyle());
        strategies.put(JTextArea.class, new TextStyle());
        strategies.put(JComboBox.class, new ComboBoxStyle());
    }

    public static void applyStyle(Container container) {
        if (container == null)
            return;

        StyleStrategy containerStrategy = strategies.get(container.getClass());
        if (containerStrategy != null) {
            containerStrategy.applyStyle(container);
        }

        for (Component component : container.getComponents()) {
            component.setFont(DEFAULT_FONT);

            StyleStrategy strategy = strategies.get(component.getClass());
            if (strategy != null) {
                strategy.applyStyle(component);
            }
            if (component instanceof Container) {
                applyStyle((Container) component);
            }
        }
    }
}