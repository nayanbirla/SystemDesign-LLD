package com.expermient.newLLD.Gate;

import com.expermient.newLLD.Vehicle.Vehicle;
import com.expermient.newLLD.parking.ParkingSlot;

import java.time.LocalDateTime;
import java.util.UUID;

public class Ticket {

    private String ticketId;
    private Vehicle vehicle;
    private ParkingSlot parkingSlot;
    private LocalDateTime entryTime;

    public Ticket(Vehicle vehicle, ParkingSlot parkingSlot){
        this.ticketId = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.parkingSlot = parkingSlot;
        this.entryTime = LocalDateTime.now();
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSlot getParkingSlot() {
        return parkingSlot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }
}


