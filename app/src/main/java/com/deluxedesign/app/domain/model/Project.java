package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "projects")
public class Project {
  @PrimaryKey @NonNull public String id = "";
  public String userId = "";
  public String vehicleId = "";
  public String presetId = "";
  public String name = "";
  public String status = "Borrador";
  public int progress;
  public long createdAt;
  public String estimatedDelivery = "";

  public Project() {}
}
