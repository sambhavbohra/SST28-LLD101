package com.parkinglot;

import java.util.Map;

public abstract class Slot {
    private final String slotId;
    private final int level;
    private boolean available;
    private VehicleDetails parkedVehicle;
    private final Map<Gate, Integer> gateDistances;

    protected Slot(String slotId, int level, Map<Gate, Integer> gateDistances) {
        this.slotId = slotId;
        this.level = level;
        this.gateDistances = gateDistances;
        this.available = true;
    }

    public abstract double getPricePerHour();

    public int distanceFrom(Gate gate) {
        Integer distance = gateDistances.get(gate);
        if (distance == null) {
            throw new IllegalArgumentException("Distance not found for gate id " + gate.getId());
        }
        return distance;
    }

    public String getSlotId() {
        return slotId;
    }

    public int getLevel() {
        return level;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public VehicleDetails getParkedVehicle() {
        return parkedVehicle;
    }

    public void setParkedVehicle(VehicleDetails parkedVehicle) {
        this.parkedVehicle = parkedVehicle;
    }

    public Map<Gate, Integer> getGateDistances() {
        return gateDistances;
    }
}
