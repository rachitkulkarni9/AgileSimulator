package com.groupesan.project.java.scrumsimulator.mainpackage.utils;

import java.util.Random;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class RandomUtils {

    private static volatile RandomUtils instance;
    private final Random random;

    @SuppressFBWarnings(value = "PREDICTABLE_RANDOM", justification = "Predictable random is used intentionally for reproducibility")
    private RandomUtils(long seed) {
        this.random = new Random(seed);
    }

    public static synchronized void resetInstance(long seed) {
        instance = new RandomUtils(seed);
    }

    public static RandomUtils getInstance() {
        if (instance == null) {
            instance = new RandomUtils(System.currentTimeMillis());
        }
        return instance;
    }

    public int getRandomInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("Bound must be greater than 0");
        }
        return random.nextInt(bound);
    }

    public int getRandomInt(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
        return random.nextInt(max - min) + min;
    }

    public double getRandomDouble() {
        return random.nextDouble();
    }

    public boolean getRandomBoolean() {
        return random.nextBoolean();
    }

    public Long getRandomLong() {
        return random.nextLong();
    }
}
