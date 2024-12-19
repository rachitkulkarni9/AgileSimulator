package com.groupesan.project.java.scrumsimulator.mainpackage.state;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerSolution;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.BlockerType;
import com.groupesan.project.java.scrumsimulator.mainpackage.core.Simulation;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.Sprint;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStory;
import com.groupesan.project.java.scrumsimulator.mainpackage.impl.UserStoryFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import static com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationFileHandler.getSimulationData;
import static com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationFileHandler.updateSimulationData;

public class SimulationSingleton {

    private static final List<Simulation> simulations = new ArrayList<>();
    private static SimulationSingleton instance;

    private SimulationSingleton() {
        // empty for now as methods in 'SimulationStateManager' are static
    }

    public static synchronized SimulationSingleton getInstance() {
        if (instance == null) {
            instance = new SimulationSingleton();
            loadSimulations();
        }
        return instance;
    }

    public List<Simulation> getAllSimulations() {
        return simulations;
    }

    public void addSimulation(Simulation simulation) {
        simulations.add(simulation);
        SimulationStateManager.getInstance().setCurrentSimulation(simulation);
        saveSimulationDetails();
    }

    /**
     * Saves the details of a new simulation to a JSON file.
     *
     */
    public void saveSimulationDetails() {
        JSONArray simulationsArray = new JSONArray();
        simulations.forEach(simulation -> simulationsArray.put(simulationToJson(simulation)));

        updateSimulationData(simulationsArray);
    }

    public Simulation getLatestSimulation() {
        if (simulations.isEmpty()) return null;
        return simulations.getLast();
    }

    public Simulation getSimulationById(UUID id) {
        return simulations.stream()
                .filter(simulation -> simulation.getSimulationId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void removeSimulation(Simulation simulation) {
        simulations.remove(simulation);
    }

    public void initializeDefaultSimulation() {
        Simulation simulation = new Simulation(
                UUID.randomUUID(),
                "Default",
                1,
                14,
                0
        );
        simulations.add(simulation);
        SimulationStateManager.getInstance().setCurrentSimulation(simulation);
    }

    public void initializeDefaultUserStories() {
        UserStory a = UserStoryFactory.getInstance()
                .createNewUserStory("predefinedUS1", "description1", 1.0, 1);

        UserStory b = UserStoryFactory.getInstance()
                .createNewUserStory("predefinedUS2", "description2", 2.0, 8);

        UserStory c = UserStoryFactory.getInstance()
                .createNewUserStory("predefinedUS3", "description3", 3.0, 13);

        SimulationStateManager.getInstance().getCurrentSimulation().addUserStories(new ArrayList<>(List.of(a, b, c)));
    }

    private static void loadSimulations() {
        JSONArray simulationsFromFile = getSimulationData();

        if (!simulationsFromFile.isEmpty()) {
            try {
                simulationsFromFile.forEach(simulation -> simulations.add(jsonToSimulation((JSONObject) simulation)));
            } catch (Exception e) {
                updateSimulationData(new JSONArray());
            }
        }
    }

    private static Simulation jsonToSimulation(JSONObject simulationJson) {
        ObjectMapper mapper = new ObjectMapper();
        JSONArray sprintsFromJson = simulationJson.getJSONArray("Sprints");
        List<Sprint> sprints = new ArrayList<>();
        if (!sprintsFromJson.isEmpty()) {
            sprintsFromJson.forEach(sprintJson -> {
                try {
                    sprints.add(mapper.readValue(sprintJson.toString(), Sprint.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        JSONArray userStoriesFromJson = simulationJson.getJSONArray("UserStories");
        List<UserStory> userStories = new ArrayList<>();
        if (!userStoriesFromJson.isEmpty()) {
            userStoriesFromJson.forEach(userStoryJson -> {
                try {
                    userStories.add(mapper.readValue(userStoryJson.toString(), UserStory.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        JSONArray blockerTypesFromJson = simulationJson.getJSONArray("BlockerTypes");
        List<BlockerType> blockerTypes = new ArrayList<>();
        if (!blockerTypesFromJson.isEmpty()) {
            blockerTypesFromJson.forEach(blockerType -> {
                try {
                    blockerTypes.add(mapper.readValue(blockerType.toString(), BlockerType.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        JSONArray blockerSolutionsFromJson = simulationJson.getJSONArray("BlockerSolutions");
        List<BlockerSolution> blockerSolutions = new ArrayList<>();
        if (!blockerTypesFromJson.isEmpty()) {
            blockerSolutionsFromJson.forEach(blockerSolution -> {
                try {
                    blockerSolutions.add(mapper.readValue(blockerSolution.toString(), BlockerSolution.class));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        return new Simulation(
                UUID.fromString(simulationJson.getString("ID")),
                simulationJson.getString("Name"),
                simulationJson.getInt("Count"),
                simulationJson.getInt("DurationDays"),
                sprints,
                userStories,
                simulationJson.getLong("Seed"),
                blockerTypes,
                blockerSolutions
        );
    }

    private static JSONObject simulationToJson(Simulation simulation) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ID", simulation.getSimulationId());
        jsonObject.put("Name", simulation.getName());
        jsonObject.put("Seed", simulation.getRandomSeed());
        jsonObject.put("Status", "New");
        jsonObject.put("DurationDays", simulation.getSprintDuration());
        jsonObject.put("Count", simulation.getSprintCount());
        jsonObject.put("Sprints", simulation.getSprints());
        jsonObject.put("UserStories", simulation.getUserStories());
        jsonObject.put("BlockerTypes", simulation.getBlockerTypes());
        jsonObject.put("BlockerSolutions", simulation.getBlockerSolutions());
        return jsonObject;
    }
}
