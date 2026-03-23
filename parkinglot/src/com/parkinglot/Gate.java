package com.parkinglot;

import java.util.Objects;

public class Gate {
    private final int id;

    public Gate(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Gate gate = (Gate) other;
        return id == gate.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
