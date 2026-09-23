package com.example.deluxedesignv42.model;

public class DesignConfiguration {
    private Vehicle vehicle;
    private String angle;
    private String paintColor;
    private String finishType;
    private String vinyl;
    private String rims;
    private String bodyKit;
    private String headlights;
    private String accessories;
    private String interiors;

    public DesignConfiguration(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    // Getters and Setters
    public Vehicle getVehicle() { return vehicle; }
    public String getAngle() { return angle; }
    public void setAngle(String angle) { this.angle = angle; }
    public String getPaintColor() { return paintColor; }
    public void setPaintColor(String paintColor) { this.paintColor = paintColor; }
    public String getFinishType() { return finishType; }
    public void setFinishType(String finishType) { this.finishType = finishType; }
    public String getVinyl() { return vinyl; }
    public void setVinyl(String vinyl) { this.vinyl = vinyl; }
    public String getRims() { return rims; }
    public void setRims(String rims) { this.rims = rims; }
    public String getBodyKit() { return bodyKit; }
    public void setBodyKit(String bodyKit) { this.bodyKit = bodyKit; }
    public String getHeadlights() { return headlights; }
    public void setHeadlights(String headlights) { this.headlights = headlights; }
    public String getAccessories() { return accessories; }
    public void setAccessories(String accessories) { this.accessories = accessories; }
    public String getInteriors() { return interiors; }
    public void setInteriors(String interiors) { this.interiors = interiors; }
}