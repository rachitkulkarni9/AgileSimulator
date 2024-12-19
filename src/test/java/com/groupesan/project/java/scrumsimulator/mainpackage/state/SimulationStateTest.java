package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.*;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.groupesan.project.java.scrumsimulator.mainpackage.core.Simulation;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager.SprintStateEnum;

public class SimulationStateTest {

    private SimulationStateManager simulationStateManager;

    @BeforeEach
    public void setUp() {
        try {
            simulationStateManager = SimulationStateManager.getInstance();
        } catch (HeadlessException he) {
            //Expected error
        }

    }

    @AfterEach
    public void tearDown() {
        if (simulationStateManager != null) {
            simulationStateManager.stopSimulation();
        }
    }

    @Test
    public void testInitialState() {
        if (simulationStateManager != null) {
            assertFalse(simulationStateManager.getState() == SprintStateEnum.RUNNING);
        } else {
            System.out.println("simulationStateManager is null");
        }
    }

    @Test
    public void testStartSimulation() {
        if (simulationStateManager != null) {
            try {
                simulationStateManager.setCurrentSimulation(
                        new Simulation(UUID.randomUUID(), "Test Simulation", 0, 0, 0));
                simulationStateManager.startSimulation();
                assertTrue(simulationStateManager.getState() == SprintStateEnum.RUNNING);
            } catch (HeadlessException e) {
                // Expected exception
            }
        }
    }
    @Test
    public void testStopSimulation() {

        if (simulationStateManager != null) {
            simulationStateManager.setCurrentSimulation(
                    new Simulation( UUID.randomUUID(),"Test Simulation", 0, 0, 0));
            simulationStateManager.stopSimulation();
            assertFalse(simulationStateManager.getState() == SprintStateEnum.RUNNING);
        }

    }

}