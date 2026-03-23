package com.parkinglot;

import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;
import java.util.PriorityQueue;

public class ParkingLot {
    private final Map<Gate, PriorityQueue<Slot>> sSlots;
    private final Map<Gate, PriorityQueue<Slot>> mSlots;
    private final Map<Gate, PriorityQueue<Slot>> lSlots;
    private final PricingStrategy pricingStrategy;
    private final Map<String, Slot> slotRegistry;

    public ParkingLot(
            Map<Gate, PriorityQueue<Slot>> sSlots,
            Map<Gate, PriorityQueue<Slot>> mSlots,
            Map<Gate, PriorityQueue<Slot>> lSlots,
            PricingStrategy pricingStrategy,
            Map<String, Slot> slotRegistry) {
        this.sSlots = sSlots;
        this.mSlots = mSlots;
        this.lSlots = lSlots;
        this.pricingStrategy = pricingStrategy;
        this.slotRegistry = slotRegistry;
    }

    public Ticket park(VehicleDetails vehicle, LocalTime entryTime, Gate entryGate, SlotType requestedType) {
        if (!isCompatible(vehicle.getType(), requestedType)) {
            throw new IncompatibleVehicleException("Vehicle type " + vehicle.getType() + " does not fit slot " + requestedType);
        }

        Map<Gate, PriorityQueue<Slot>> slotsByGate = getSlotsByType(requestedType);
        PriorityQueue<Slot> queueForGate = slotsByGate.get(entryGate);
        if (queueForGate == null || queueForGate.isEmpty()) {
            throw new NoSlotAvailableException("No " + requestedType + " slot available near gate " + entryGate.getId());
        }

        Slot selectedSlot = queueForGate.poll();
        removeFromOtherGateQueues(slotsByGate, entryGate, selectedSlot);
        selectedSlot.setAvailable(false);
        selectedSlot.setParkedVehicle(vehicle);

        return new Ticket(
                selectedSlot.getSlotId(),
                requestedType,
                selectedSlot.getLevel(),
                vehicle,
                entryGate,
                entryTime);
    }

    public Map<SlotType, Integer> status() {
        Map<SlotType, Integer> availableCounts = new EnumMap<>(SlotType.class);
        availableCounts.put(SlotType.S, 0);
        availableCounts.put(SlotType.M, 0);
        availableCounts.put(SlotType.L, 0);

        for (Slot slot : slotRegistry.values()) {
            if (slot.isAvailable()) {
                SlotType slotType = getTypeForSlot(slot);
                availableCounts.put(slotType, availableCounts.get(slotType) + 1);
            }
        }

        return availableCounts;
    }

    public int exit(Ticket ticket, LocalTime exitTime) {
        Slot slot = slotRegistry.get(ticket.getSlotId());
        if (slot == null) {
            throw new InvalidTicketException("Invalid ticket for slot " + ticket.getSlotId());
        }

        slot.setAvailable(true);
        slot.setParkedVehicle(null);

        SlotType slotType = getTypeForSlot(slot);
        addBackToAllGateQueues(getSlotsByType(slotType), slot);

        double fee = pricingStrategy.calculate(ticket.getEntryTime(), exitTime, slot.getPricePerHour());
        return (int) Math.ceil(fee);
    }

    private boolean isCompatible(VehicleType vehicleType, SlotType slotType) {
        if (slotType == SlotType.S) {
            return vehicleType == VehicleType.TWO_WHEELER;
        }
        if (slotType == SlotType.M) {
            return vehicleType == VehicleType.TWO_WHEELER || vehicleType == VehicleType.FOUR_M;
        }
        return true;
    }

    private Map<Gate, PriorityQueue<Slot>> getSlotsByType(SlotType slotType) {
        if (slotType == SlotType.S) {
            return sSlots;
        }
        if (slotType == SlotType.M) {
            return mSlots;
        }
        return lSlots;
    }

    private void removeFromOtherGateQueues(Map<Gate, PriorityQueue<Slot>> slotsByGate, Gate entryGate, Slot slot) {
        for (Map.Entry<Gate, PriorityQueue<Slot>> entry : slotsByGate.entrySet()) {
            if (!entry.getKey().equals(entryGate)) {
                entry.getValue().remove(slot);
            }
        }
    }

    private void addBackToAllGateQueues(Map<Gate, PriorityQueue<Slot>> slotsByGate, Slot slot) {
        for (PriorityQueue<Slot> queue : slotsByGate.values()) {
            queue.offer(slot);
        }
    }

    private SlotType getTypeForSlot(Slot slot) {
        if (slot instanceof SlotS) {
            return SlotType.S;
        }
        if (slot instanceof SlotM) {
            return SlotType.M;
        }
        return SlotType.L;
    }
}
