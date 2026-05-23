package com.expermient.newLLD.pricingStrategy;

import com.expermient.newLLD.type.VehicleType;

public interface Pricing {

    public double calculatePrice(VehicleType vehicleType, long parkedMinutes);
}
