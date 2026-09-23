package com.example.deluxedesignv42.data.local;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class ProjectEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String name;
    public String vehicleId;
    public String angle;
    public String paintColor;
    public String finishType;
    public long dateLong;
    public String status;
    public int progressPercentage;
    public int previewImageResId;
}