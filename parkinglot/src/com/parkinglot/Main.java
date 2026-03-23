package com.parkinglot;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<Gate> gates = List.of(new Gate(1), new Gate(2), new Gate(3));

        Map<Integer, Map<SlotType, Integer>> levelSlotMapping = new HashMap<>();
        levelSlotMapping.put(1, buildCounts(2, 2, 1));
        levelSlotMapping.put(2, buildCounts(2, 1, 2));

        Map<SlotType, Double> pricesPerHour = new EnumMap<>(SlotType.class);
        pricesPerHour.put(SlotType.S, 20.0);
        pricesPerHour.put(SlotType.M, 40.0);
        pricesPerHour.put(SlotType.L, 60.0);

        List<String> allSlotIds = generateSlotIds(2, levelSlotMapping);
        Map<Gate, Map<String, Integer>> gateSlotDistances = buildGateSlotDistances(gates, allSlotIds);

        ParkingLot parkingLot = ParkingLotFactory.create(
                2,
                levelSlotMapping,
                gates,
                gateSlotDistances,
                pricesPerHour,
                new HourlyPricing());

        System.out.println("Initial status: " + parkingLot.status());

        Ticket twoWheelerTicket = parkingLot.park(
                new VehicleDetails("DL01AA1001", VehicleType.TWO_WHEELER, "Aman"),
                LocalTime.of(9, 10),
                gates.get(0),
                SlotType.S);

        Ticket sedanTicket = parkingLot.park(
                new VehicleDetails("DL01BB2002", VehicleType.FOUR_M, "Riya"),
                LocalTime.of(9, 25),
                gates.get(1),
                SlotType.M);

        Ticket suvTicket = parkingLot.park(
                new VehicleDetails("DL01CC3003", VehicleType.FOUR_L, "Kabir"),
                LocalTime.of(9, 40),
                gates.get(2),
                SlotType.L);

        System.out.println("Status after parking: " + parkingLot.status());

        int feeForSedan = parkingLot.exit(sedanTicket, LocalTime.of(12, 5));
        System.out.println("Sedan ticket slot: " + sedanTicket.getSlotId());
        System.out.println("Sedan parking fee: " + feeForSedan);

        int feeForTwoWheeler = parkingLot.exit(twoWheelerTicket, LocalTime.of(10, 5));
        System.out.println("Two wheeler ticket slot: " + twoWheelerTicket.getSlotId());
        System.out.println("Two wheeler parking fee: " + feeForTwoWheeler);

        System.out.println("Status after exits: " + parkingLot.status());
        System.out.println("SUV ticket still active in slot: " + suvTicket.getSlotId());
    }

    private static Map<SlotType, Integer> buildCounts(int sCount, int mCount, int lCount) {
        Map<SlotType, Integer> counts = new EnumMap<>(SlotType.class);
        counts.put(SlotType.S, sCount);
        counts.put(SlotType.M, mCount);
        counts.put(SlotType.L, lCount);
        return counts;
    }

    private static List<String> generateSlotIds(int levels, Map<Integer, Map<SlotType, Integer>> levelSlotMapping) {
        List<String> slotIds = new ArrayList<>();
        for (int level = 1; level <= levels; level++) {
            Map<SlotType, Integer> counts = levelSlotMapping.getOrDefault(level, new EnumMap<>(SlotType.class));
            for (SlotType slotType : SlotType.values()) {
                int total = counts.getOrDefault(slotType, 0);
                for (int index = 1; index <= total; index++) {
                    slotIds.add("L" + level + "-" + slotType + "-" + index);
                }
            }
        }
        return slotIds;
    }

    private static Map<Gate, Map<String, Integer>> buildGateSlotDistances(List<Gate> gates, List<String> slotIds) {
        Map<Gate, Map<String, Integer>> gateDistances = new HashMap<>();

        for (Gate gate : gates) {
            Map<String, Integer> distances = new HashMap<>();
            for (String slotId : slotIds) {
                String[] parts = slotId.split("-");
                int level = Integer.parseInt(parts[0].substring(1));
                String typeToken = parts[1];
                int index = Integer.parseInt(parts[2]);

                int typeWeight = 0;
                if ("M".equals(typeToken)) {
                    typeWeight = 4;
                } else if ("L".equals(typeToken)) {
                    typeWeight = 8;
                }

                int distance = gate.getId() * 10 + level * 3 + index + typeWeight;
                distances.put(slotId, distance);
            }
            gateDistances.put(gate, distances);
        }

        return gateDistances;
    }
}
