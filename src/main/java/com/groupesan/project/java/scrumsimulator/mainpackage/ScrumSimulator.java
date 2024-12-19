package com.groupesan.project.java.scrumsimulator.mainpackage;

import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationSingleton;
import com.groupesan.project.java.scrumsimulator.mainpackage.ui.App;

public class ScrumSimulator {
    public static void main(String[] args) {
        new App().start();

        // Reference https://www.geeksforgeeks.org/jvm-shutdown-hook-java/
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> SimulationSingleton.getInstance().saveSimulationDetails()));
    }
}
