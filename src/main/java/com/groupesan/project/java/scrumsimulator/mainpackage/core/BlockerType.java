package com.groupesan.project.java.scrumsimulator.mainpackage.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlockerType {

    private String name;
    private int encounterChance;
    private int resolveChance;
    private int spikeChance;

    @JsonCreator
    public BlockerType(@JsonProperty("name") String name,
                       @JsonProperty("encounterChance") int encounterChance,
                       @JsonProperty("resolveChance") int resolveChance,
                       @JsonProperty("spikeChance") int spikeChance) {
        this.name = name;
        this.encounterChance = encounterChance;
        this.resolveChance = resolveChance;
        this.spikeChance = spikeChance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEncounterChance() {
        return encounterChance;
    }

    public void setEncounterChance(int encounterChance) {
        this.encounterChance = encounterChance;
    }

    public int getResolveChance() {
        return resolveChance;
    }

    public void setResolveChance(int resolveChance) {
        this.resolveChance = resolveChance;
    }

    public int getSpikeChance() {
        return spikeChance;
    }

    public void setSpikeChance(int spikeChance) {
        this.spikeChance = spikeChance;
    }

    public String toString() {
        return "[Blocker] " + name;
    }

    public BlockerType deepClone() {
        return new BlockerType(
                this.name,
                this.getEncounterChance(),
                this.getResolveChance(),
                this.spikeChance
        );
    }
}
