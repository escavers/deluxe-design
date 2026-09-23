package com.example.deluxedesignv42.model;

import java.util.Date;

public class Project {
    private String id;
    private String userId;
    private String name;
    private DesignConfiguration configuration;
    private int previewImageResId;
    private Date date;
    private String status;
    private int progressPercentage;

    public Project(String id, String userId, String name, DesignConfiguration config, 
                   int previewImageResId, Date date, String status, int progressPercentage) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.configuration = config;
        this.previewImageResId = previewImageResId;
        this.date = date;
        this.status = status;
        this.progressPercentage = progressPercentage;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public DesignConfiguration getConfiguration() { return configuration; }
    public int getPreviewImageResId() { return previewImageResId; }
    public Date getDate() { return date; }
    public String getStatus() { return status; }
    public int getProgressPercentage() { return progressPercentage; }
}