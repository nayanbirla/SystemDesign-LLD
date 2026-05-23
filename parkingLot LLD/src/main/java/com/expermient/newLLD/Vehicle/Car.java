package com.expermient.newLLD.Vehicle;

import com.expermient.newLLD.type.VehicleType;

public class Car extends Vehicle{

    public Car(String licensePlate) {
        super(licensePlate, VehicleType.CAR);
    }
}
