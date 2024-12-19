package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


public class SimulationFileHandler {
    private static String SIMULATION_JSON_FILE_PATH = System.getProperty("user.home") + "/simulation.json";

    public static void setSimulationJsonFilePath(String filePath) {
        SIMULATION_JSON_FILE_PATH = filePath;
    }

    @SuppressFBWarnings(value = "PATH_TRAVERSAL_IN", justification="This method is only called in test cases")
    private static File getSimulationJsonFile() {
        File file = new File(SIMULATION_JSON_FILE_PATH);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    try (OutputStreamWriter writer = new OutputStreamWriter(
                            new FileOutputStream(file), StandardCharsets.UTF_8)) {
                        writer.write(new JSONArray().toString(4));
                    }
                }
            } catch (IOException e) {
                throw new IllegalArgumentException("Unable to create " + SIMULATION_JSON_FILE_PATH, e);
            }
        }
        return file;
    }

    public static JSONArray getSimulationData() {
        try (FileInputStream fis = new FileInputStream(getSimulationJsonFile())) {
            JSONTokener tokener = new JSONTokener(fis);
            return new JSONArray(tokener);
        } catch (IOException | JSONException e) {
            System.err.println("Error reading from " + SIMULATION_JSON_FILE_PATH);
            return new JSONArray();
        }
    }

    public static void updateSimulationData(JSONArray updatedData) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(getSimulationJsonFile()), StandardCharsets.UTF_8)) {
            writer.write(updatedData.toString(4));
        } catch (IOException e) {
            System.err.println("Error writing to " + SIMULATION_JSON_FILE_PATH);
            JOptionPane.showMessageDialog(null, "Error writing to " + SIMULATION_JSON_FILE_PATH);
        }
    }
}
