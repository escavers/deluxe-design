package com.example.deluxedesignv42.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quotes")
public class QuoteEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int projectId;
    public String projectName;
    public String vehicleName;
    public String clientName;
    public String notes;
    public String status;
    public long dateLong;
    public double estimatedAmount;
}