package com.groupesan.project.java.scrumsimulator.mainpackage.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.groupesan.project.java.scrumsimulator.mainpackage.state.SimulationStateManager;
import com.groupesan.project.java.scrumsimulator.mainpackage.utils.RandomUtils;

public class BlockerObject {

    @JsonProperty
    private final BlockerType type;

    public enum BlockerState {
        UNRESOLVED,
        SPIKED,
        RESOLVED
    }

    @JsonProperty
    private volatile BlockerState state;

    @JsonProperty
    private BlockerSolution solution;

    public BlockerObject(BlockerType type) {
        this.type = type;
        this.state = BlockerState.UNRESOLVED;
    }

    @SuppressWarnings("unused")
    @JsonCreator
    public BlockerObject(
            @JsonProperty("type") BlockerType type,
            @JsonProperty("solution") BlockerSolution solution,
            @JsonProperty("state") String state) {
        this.type = type;
        this.solution = solution;
        this.state = BlockerState.valueOf(state);
    }

    public BlockerType getType() {
        return type;
    }

    public String toString() {
        return "[Blocker] " + type.toString();
    }

    public boolean attemptResolve() {
        int resolveChance = type.getResolveChance();

        if (state == BlockerState.SPIKED) {
            resolveChance /= 2;
        }

        if (RandomUtils.getInstance().getRandomInt(100) < resolveChance) {
            this.solution = SimulationStateManager.getInstance().getRandomBlockerSolution();
            return true;
        } else {

            if (RandomUtils.getInstance().getRandomInt(100) < type.getSpikeChance()) {
                state = BlockerState.SPIKED;
            }

            return false;
        }
    }

    public void resolve() {
        this.state = BlockerState.RESOLVED;
    }

    @JsonIgnore
    public boolean isResolved() {
        return state == BlockerState.RESOLVED;
    }

    public BlockerSolution getSolution() {
        return solution;
    }

    public BlockerState getState() {
        return this.state;
    }
}
