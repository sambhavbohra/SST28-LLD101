package com.pen.model;

import com.pen.enums.InkColour;

public class Ink {
    public static final double FULL_LEVEL = 1.0;
    public static final double EMPTY_LEVEL = 0.0;
    public static final double UNITS_CONSUMED_PER_WRITE = 0.05;
    private static final int PERCENTAGE_MULTIPLIER = 100;

    private final InkColour colour;
    private double level;

    private Ink(InkColour colour, double level) {
        this.colour = colour;
        this.level = level;
    }

    public static Ink fullCartridge(InkColour colour) {
        return new Ink(colour, FULL_LEVEL);
    }

    public void consume() {
        level = Math.max(EMPTY_LEVEL, level - UNITS_CONSUMED_PER_WRITE);
    }

    public boolean isEmpty() {
        return level <= EMPTY_LEVEL;
    }

    public double getLevel() {
        return level;
    }

    public InkColour getColour() {
        return colour;
    }

    public String levelAsPercentage() {
        int percentage = (int) Math.round(level * PERCENTAGE_MULTIPLIER);
        return percentage + "%";
    }
}
