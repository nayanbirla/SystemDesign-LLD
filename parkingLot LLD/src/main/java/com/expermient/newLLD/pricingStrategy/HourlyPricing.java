package com.expermient.newLLD.pricingStrategy;

import com.expermient.newLLD.type.VehicleType;

public class HourlyPricing implements Pricing{

    public double calculatePrice(VehicleType vehicleType,long parkedMinutes){

        long hours = (long) Math.ceil(parkedMinutes/60.0);
        if(hours==0){
            hours=1;
        }

        return switch (vehicleType) {
            case BIKE-> hours*10;
            case CAR-> hours*20;
            case TRUCK-> hours*30;
        };

    }
}
