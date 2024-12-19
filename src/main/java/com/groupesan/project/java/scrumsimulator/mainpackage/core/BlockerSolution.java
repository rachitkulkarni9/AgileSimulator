package com.groupesan.project.java.scrumsimulator.mainpackage.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockerSolution {
    private String name;
    private int chance;

    @JsonCreator
    public BlockerSolution(@JsonProperty("name") String name, @JsonProperty("chance") int chance) {
        this.name = name;
        this.chance = chance;
    }

    public String getName() {
        return name;
    }

    public int getChance() {
        return chance;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }

    public BlockerSolution deepClone() {
        return new BlockerSolution(
                this.name,
                this.chance
        );
    }
}
