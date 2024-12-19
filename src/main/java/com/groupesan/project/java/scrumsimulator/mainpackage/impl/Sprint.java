package com.groupesan.project.java.scrumsimulator.mainpackage.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class Sprint {
    @JsonProperty
    private final ArrayList<UserStory> userStories = new ArrayList<>();

    private final String name;

    @JsonProperty
    private final String description;

    @JsonProperty
    private final int length;

    private final int id;

    public Sprint(String name, String description, int length, int id) {
        this.name = name;
        this.description = description;
        this.length = length;
        this.id = id;
    }

    @JsonCreator
    public Sprint(@JsonProperty("length") int length,
                  @JsonProperty("userStories") List<UserStory> userStories,
                  @JsonProperty("id") int id) {
        this.name = "";
        this.description = "";
        this.length = length;
        this.userStories.addAll(userStories);
        this.id = id;
    }

    public void addUserStory(UserStory us) {
        userStories.add(us);
    }

    public void removeUserStory(UUID id) {
        Optional<UserStory> storyToRemove = userStories.stream()
                .filter(userStory -> userStory.getId().equals(id))
                .toList()
                .stream()
                .findFirst();
        if (storyToRemove.isEmpty()) return;
        userStories.remove(storyToRemove.get());
    }


    public List<UserStory> getUserStories() {
        return new ArrayList<>(userStories);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getLength() {
        return length;
    }

    public int getId() {
        return id;
    }

    public String toString() {
        return "Sprint " + this.id + "\n";
    }

    public void clearUserStories() {
        userStories.clear();
    }

    public Sprint deepClone() {
        List<UserStory> clonedUserStories = new ArrayList<>();
        userStories.forEach(userStory -> clonedUserStories.add(userStory.deepClone()));

        return new Sprint(
                this.length,
                clonedUserStories,
                this.id
        );
    }
}
