package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class Favorite {
  @PrimaryKey @NonNull public String id = "";
  public String userId = "";
  public String vehicleId = "";

  public Favorite() {}
}
