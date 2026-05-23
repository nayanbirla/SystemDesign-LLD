package com.expermient.newLLD.parking;

import com.expermient.newLLD.Vehicle.Vehicle;
import com.expermient.newLLD.type.SlotType;
import com.expermient.newLLD.type.VehicleType;

import java.util.ArrayList;
import java.util.List;

public class Level {

    private int level;
    private List<ParkingSlot> parkingSlot;


    public Level(int level,int small,int medium,int large){
        this.level=level;
        parkingSlot = new ArrayList<>();
        for(int i=1;i<=small;i++){
            parkingSlot.add(new ParkingSlot("L"+level+"S"+i,SlotType.SMALL));
        }
        for(int i=1;i<=medium;i++){
            parkingSlot.add(new ParkingSlot("L"+level+"M"+i,SlotType.MEDIUM));
        }
        for(int i=1;i<=large;i++){
            parkingSlot.add(new ParkingSlot("L"+level+"L"+i,SlotType.LARGE));
        }
    }

    public synchronized ParkingSlot availableSlot(VehicleType vehicleType) {

        return parkingSlot.stream()
                .filter(slot -> slot.canVehicleFit(vehicleType))
                .findFirst()
                .orElse(null);
    }

    public int getLevelNumber() {
        return level;
    }
}
