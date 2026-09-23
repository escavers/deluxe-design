package com.example.deluxedesignv42.model;

import java.util.List;

public class Vehicle {
    private String id;
    private String brand;
    private String model;
    private int year;
    private String category;
    private String description;
    private List<Integer> imageResIds;
    private VehicleSpecification specifications;

    public Vehicle(String id, String brand, String model, int year, String category, 
                   String description, List<Integer> imageResIds, VehicleSpecification specs) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.category = category;
        this.description = description;
        this.imageResIds = imageResIds;
        this.specifications = specs;
    }

    // Getters
    public String getId() { return id; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public List<Integer> getImageResIds() { return imageResIds; }
    public VehicleSpecification getSpecifications() { return specifications; }
    
    public String getFullName() {
        return brand + " " + model + " (" + year + ")";
    }
}