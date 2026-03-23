# Parking Lot

## Overview

This project is a Java implementation of a parking lot system built as a low-level design exercise.

It supports:
- Multiple gates
- Multiple levels
- Slot categories by size
- Vehicle-to-slot compatibility checks
- Nearest-slot allocation per gate using priority queues
- Entry ticket generation and exit fee calculation through strategy pattern

## Domain Model

- Gate: Identified by integer id and used as a key in maps.
- SlotType: S, M, L.
- VehicleType: TWO_WHEELER, FOUR_M, FOUR_L.
- VehicleDetails: registration number, vehicle type, owner name.
- Ticket: slot id, slot type, level, vehicle, entry gate, entry time.

## Slot Design

- Slot is an abstract class with:
	- slotId
	- level
	- availability
	- currently parked vehicle
	- distance map from each gate
- SlotS, SlotM, SlotL are concrete slot types with configurable hourly prices.

## Pricing

- PricingStrategy defines fee calculation contract.
- HourlyPricing rounds parking duration up to the nearest hour.

Example:
- 2 hours 1 minute is billed as 3 hours.

## ParkingLot Core Logic

ParkingLot keeps:
- One map per slot type: Gate to PriorityQueue of Slot
- Priority queue order: shortest distance from that gate, then slot id
- slotRegistry for O(1) lookup by slot id
- pricingStrategy for fee calculation

### park(vehicle, entryTime, entryGate, requestedType)

- Validates vehicle compatibility:
	- S: only TWO_WHEELER
	- M: TWO_WHEELER, FOUR_M
	- L: all vehicle types
- Picks the nearest available slot from requested type queue for entry gate
- Removes that slot from all gates for that type
- Marks slot unavailable and attaches vehicle
- Returns a Ticket

Throws:
- IncompatibleVehicleException
- NoSlotAvailableException

### status()

- Returns available slot counts by SlotType.

### exit(ticket, exitTime)

- Finds slot by ticket slot id
- Marks slot available and clears parked vehicle
- Adds slot back to all gate queues for its type
- Calculates fee via PricingStrategy and returns integer amount

Throws:
- InvalidTicketException

## Factory

ParkingLotFactory.create(...) builds and wires the full system:
- Creates slot ids in format: L{level}-{type}-{index}
- Builds Slot objects with gate distances and per-type prices
- Builds gate-wise priority queues for each slot type
- Builds slotRegistry
- Injects pricing strategy

## Project Structure

- src/com/parkinglot/Gate.java
- src/com/parkinglot/SlotType.java
- src/com/parkinglot/VehicleType.java
- src/com/parkinglot/VehicleDetails.java
- src/com/parkinglot/Ticket.java
- src/com/parkinglot/PricingStrategy.java
- src/com/parkinglot/HourlyPricing.java
- src/com/parkinglot/Slot.java
- src/com/parkinglot/SlotS.java
- src/com/parkinglot/SlotM.java
- src/com/parkinglot/SlotL.java
- src/com/parkinglot/ParkingLot.java
- src/com/parkinglot/ParkingLotFactory.java
- src/com/parkinglot/NoSlotAvailableException.java
- src/com/parkinglot/IncompatibleVehicleException.java
- src/com/parkinglot/InvalidTicketException.java
- src/com/parkinglot/Main.java

## How to Run

Use Java 8+.

From project root:

```bash
cd parkinglot/src
javac com/parkinglot/*.java
java com.parkinglot.Main
```

## Sample Output Shape

The program prints:
- Initial available slot counts
- Status after parking vehicles
- Slot ids and fees for exited tickets
- Final status after exits

## Notes

- This implementation is ready for extension with new pricing strategies and slot-selection rules.
- Distances are injected at creation time, so gate-aware slot ordering is fully configurable.