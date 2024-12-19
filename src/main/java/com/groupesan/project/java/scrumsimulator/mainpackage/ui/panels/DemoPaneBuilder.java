package com.groupesan.project.java.scrumsimulator.mainpackage.ui.panels;

import com.groupesan.project.java.scrumsimulator.mainpackage.utils.CustomConstraints;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DemoPaneBuilder {
    private record ComponentConfig(Component component, int xPosition, int yPosition) {
    }

    private final JPanel panel;
    private final List<ComponentConfig> componentList;

    public DemoPaneBuilder(JPanel panel) {
        this.panel = panel;
        this.componentList = new ArrayList<>();
    }

    public DemoPaneBuilder addComponent(Component component, int x, int y) {
        componentList.add(new ComponentConfig(component, x, y));
        return this;
    }

    public void buildPanel() {
        for (ComponentConfig c : componentList) {
            panel.add(c.component,
                    new CustomConstraints(
                            c.xPosition,
                            c.yPosition,
                            GridBagConstraints.WEST,
                            1.0,
                            1.0,
                            GridBagConstraints.HORIZONTAL));
        }
    }
}
