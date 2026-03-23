package com.parkinglot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ParkingLotFactory {
    public static ParkingLot create(
            int levels,
            Map<Integer, Map<SlotType, Integer>> levelSlotMapping,
            List<Gate> gates,
            Map<Gate, Map<String, Integer>> gateSlotDistances,
            Map<SlotType, Double> pricesPerHour,
            PricingStrategy pricingStrategy) {

        Map<String, Slot> slotRegistry = new HashMap<>();
        Map<SlotType, List<Slot>> slotsByType = new EnumMap<>(SlotType.class);
        slotsByType.put(SlotType.S, new ArrayList<>());
        slotsByType.put(SlotType.M, new ArrayList<>());
        slotsByType.put(SlotType.L, new ArrayList<>());

        for (int level = 1; level <= levels; level++) {
            Map<SlotType, Integer> typeCounts = levelSlotMapping.getOrDefault(level, new EnumMap<>(SlotType.class));

            for (SlotType slotType : SlotType.values()) {
                int count = typeCounts.getOrDefault(slotType, 0);
                for (int index = 1; index <= count; index++) {
                    String slotId = buildSlotId(level, slotType, index);
                    Map<Gate, Integer> distancesForSlot = buildDistancesForSlot(slotId, gates, gateSlotDistances);
                    Slot slot = buildSlot(slotId, level, slotType, distancesForSlot, pricesPerHour);
                    slotRegistry.put(slotId, slot);
                    slotsByType.get(slotType).add(slot);
                }
            }
        }

        Map<Gate, PriorityQueue<Slot>> sSlots = buildQueuesForType(gates, slotsByType.get(SlotType.S));
        Map<Gate, PriorityQueue<Slot>> mSlots = buildQueuesForType(gates, slotsByType.get(SlotType.M));
        Map<Gate, PriorityQueue<Slot>> lSlots = buildQueuesForType(gates, slotsByType.get(SlotType.L));

        return new ParkingLot(sSlots, mSlots, lSlots, pricingStrategy, slotRegistry);
    }

    private static String buildSlotId(int level, SlotType slotType, int index) {
        return "L" + level + "-" + slotType + "-" + index;
    }

    private static Map<Gate, Integer> buildDistancesForSlot(
            String slotId,
            List<Gate> gates,
            Map<Gate, Map<String, Integer>> gateSlotDistances) {
        Map<Gate, Integer> distancesForSlot = new HashMap<>();
        for (Gate gate : gates) {
            Map<String, Integer> distancesFromGate = gateSlotDistances.get(gate);
            if (distancesFromGate == null || !distancesFromGate.containsKey(slotId)) {
                throw new IllegalArgumentException("Missing distance for gate " + gate.getId() + " and slot " + slotId);
            }
            distancesForSlot.put(gate, distancesFromGate.get(slotId));
        }
        return distancesForSlot;
    }

    private static Slot buildSlot(
            String slotId,
            int level,
            SlotType slotType,
            Map<Gate, Integer> gateDistances,
            Map<SlotType, Double> pricesPerHour) {
        Double price = pricesPerHour.get(slotType);
        if (price == null) {
            throw new IllegalArgumentException("Missing price for slot type " + slotType);
        }

        if (slotType == SlotType.S) {
            return new SlotS(slotId, level, gateDistances, price);
        }
        if (slotType == SlotType.M) {
            return new SlotM(slotId, level, gateDistances, price);
        }
        return new SlotL(slotId, level, gateDistances, price);
    }

    private static Map<Gate, PriorityQueue<Slot>> buildQueuesForType(List<Gate> gates, List<Slot> slots) {
        Map<Gate, PriorityQueue<Slot>> queues = new HashMap<>();
        for (Gate gate : gates) {
            Comparator<Slot> byDistanceThenId = Comparator
                    .comparingInt((Slot slot) -> slot.distanceFrom(gate))
                    .thenComparing(Slot::getSlotId);

            PriorityQueue<Slot> queue = new PriorityQueue<>(byDistanceThenId);
            queue.addAll(slots);
            queues.put(gate, queue);
        }
        return queues;
    }
}
