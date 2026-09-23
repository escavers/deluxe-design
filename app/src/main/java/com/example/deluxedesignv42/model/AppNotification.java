package com.example.deluxedesignv42.model;

import java.util.Date;

public class AppNotification {
    private String id;
    private String title;
    private String message;
    private Date date;
    private boolean isRead;

    public AppNotification(String id, String title, String message, Date date, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.isRead = isRead;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Date getDate() { return date; }
    public boolean isRead() { return isRead; }
}