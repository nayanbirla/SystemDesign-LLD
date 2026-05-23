package com.expermient.newLLD.parking;

import com.expermient.newLLD.Vehicle.Vehicle;
import com.expermient.newLLD.type.SlotType;
import com.expermient.newLLD.type.VehicleType;

public class ParkingSlot {

    private String slotId;
    private SlotType slotType;
    private boolean isOccupied;
    private Vehicle parkedVehicle;

    public ParkingSlot(String slotId, SlotType slotType) {
        this.slotId = slotId;
        this.slotType = slotType;
        this.isOccupied = false;
        this.parkedVehicle = null;
    }

    public boolean canVehicleFit(VehicleType vehicleType){
        return !isOccupied && slotType == getRequiredSlotType(vehicleType);
    }

    private SlotType getRequiredSlotType(VehicleType vehicleType) {
        return switch (vehicleType){
            case BIKE-> SlotType.SMALL;
            case CAR-> SlotType.MEDIUM;
            case TRUCK-> SlotType.LARGE;
        };
    }

    public void assignVehicle(Vehicle vehicle){
        this.isOccupied=true;
        this.parkedVehicle=vehicle;
    }

    public void removeVehicle(){
        this.isOccupied=false;
        this.parkedVehicle=null;
    }

    public String getSlotId(){
        return slotId;
    }


}
