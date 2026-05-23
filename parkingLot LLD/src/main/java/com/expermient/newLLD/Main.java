package com.expermient.newLLD;

import com.expermient.newLLD.Gate.Ticket;
import com.expermient.newLLD.Vehicle.Car;
import com.expermient.newLLD.Vehicle.Vehicle;
import com.expermient.newLLD.parking.Level;
import com.expermient.newLLD.parking.ParkingLot;
import com.expermient.newLLD.pricingStrategy.HourlyPricing;
import com.expermient.newLLD.pricingStrategy.Pricing;

public class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = ParkingLot.getInstance();
        parkingLot.addLevel(new Level(1,10,10,10));
        parkingLot.addLevel(new Level(2,10,10,10));

        Pricing pricing = new HourlyPricing();

        Vehicle car = new Car("MP-09-UP-3759");
        Ticket ticket = parkingLot.parkVehicle(car);
        System.out.println("Parked! Ticket: " + ticket.getTicketId()
                + " | Slot: " + ticket.getParkingSlot().getSlotId());

        double moneyCharged  = parkingLot.exitVehicle(ticket, pricing);
        System.out.println("Exited! Amount Charged: " + moneyCharged);
    }
}
