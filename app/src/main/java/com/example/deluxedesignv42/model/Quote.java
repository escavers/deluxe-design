package com.example.deluxedesignv42.model;

import java.util.List;

public class Quote {
    private Project project;
    private String notes;
    private List<String> items;
    private double amount;
    private String status;

    public Quote(Project project, String notes, List<String> items, double amount, String status) {
        this.project = project;
        this.notes = notes;
        this.items = items;
        this.amount = amount;
        this.status = status;
    }

    // Getters
    public Project getProject() { return project; }
    public String getNotes() { return notes; }
    public List<String> getItems() { return items; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
}