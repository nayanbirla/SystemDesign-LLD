package com.expermient.newLLD.parking;

import com.expermient.newLLD.Gate.Ticket;
import com.expermient.newLLD.Vehicle.Vehicle;
import com.expermient.newLLD.pricingStrategy.Pricing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ParkingLot {

    private static ParkingLot instance;
    private List<Level> levels;

    private ParkingLot(){
        levels = new ArrayList<>();
    }

    public static synchronized ParkingLot getInstance() {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) {
                    instance = new ParkingLot();
                }
            }
            instance = new ParkingLot();
        }
        return instance;
    }

    public void addLevel(Level level) {
        levels.add(level);
    }

    public Ticket parkVehicle(Vehicle vehicle) {
        for (Level level : levels) {
            ParkingSlot slot = level.availableSlot(vehicle.getVehicleType());
            if (slot != null) {
                slot.assignVehicle(vehicle);
                return new Ticket(vehicle, slot);
            }
        }
        return null; // No available slot
    }

    public double exitVehicle(Ticket ticket, Pricing pricing) {
        ParkingSlot slot = ticket.getParkingSlot();
        slot.removeVehicle();

        long minutesParked = ChronoUnit.MINUTES.between(
                ticket.getEntryTime(), LocalDateTime.now()
        );

        return pricing.calculatePrice(ticket.getVehicle().getVehicleType(), minutesParked);
    }

}
