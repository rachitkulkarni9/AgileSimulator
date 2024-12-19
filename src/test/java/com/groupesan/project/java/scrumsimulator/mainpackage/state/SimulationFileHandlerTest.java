package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class SimulationFileHandlerTest {

    private static final String TEMP_FILE_PATH = "temp_test_resources/simulation.json";

    @BeforeAll
    public static void setup() throws IOException {
        File tempDir = new File("temp_test_resources");
        if (!tempDir.exists()) {
            boolean created = tempDir.mkdir();
            if (!created) {
                throw new IOException("Failed to create temp directory");
            }
        }

        Files.copy(Paths.get("src/test/resources/simulation.json"), Paths.get(TEMP_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
        SimulationFileHandler.setSimulationJsonFilePath(TEMP_FILE_PATH);
    }

    @AfterAll
    public static void cleanup() throws IOException {
        Files.deleteIfExists(Paths.get(TEMP_FILE_PATH));
    }

    @Test
    public void testLoadingFile() {
        JSONArray allSimulationData = SimulationFileHandler.getSimulationData();
        JSONObject simulationData = allSimulationData.getJSONObject(0);

        Assertions.assertEquals(1, allSimulationData.length(), "Only 1 simulation should be present");
        Assertions.assertEquals("Test Simulation", simulationData.getString("Name"));
        Assertions.assertEquals("f801aa89-8ce8-49fc-9f73-bc297eba610b", simulationData.getString("ID"));
        Assertions.assertEquals(1, simulationData.getJSONArray("Sprints").length(), "Only 1 Sprint should be present");
    }
}

