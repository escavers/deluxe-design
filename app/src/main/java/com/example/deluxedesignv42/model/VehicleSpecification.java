package com.example.deluxedesignv42.model;

public class VehicleSpecification {
    private String engine;
    private String transmission;
    private String power;
    private String traction;
    private String weight;

    public VehicleSpecification(String engine, String transmission, String power, String traction, String weight) {
        this.engine = engine;
        this.transmission = transmission;
        this.power = power;
        this.traction = traction;
        this.weight = weight;
    }

    // Getters
    public String getEngine() { return engine; }
    public String getTransmission() { return transmission; }
    public String getPower() { return power; }
    public String getTraction() { return traction; }
    public String getWeight() { return weight; }
}