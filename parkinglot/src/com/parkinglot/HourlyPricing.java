package com.parkinglot;

import java.time.Duration;
import java.time.LocalTime;

public class HourlyPricing implements PricingStrategy {
    @Override
    public double calculate(LocalTime entryTime, LocalTime exitTime, double pricePerHour) {
        if (entryTime == null || exitTime == null) {
            throw new IllegalArgumentException("Entry and exit time must be present");
        }
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        if (minutes < 0) {
            throw new IllegalArgumentException("Exit time cannot be before entry time");
        }
        if (minutes == 0) {
            return 0;
        }
        long billableHours = (minutes + 59) / 60;
        return billableHours * pricePerHour;
    }
}
