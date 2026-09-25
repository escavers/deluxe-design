package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vehicles")
public class Vehicle {
  @PrimaryKey @NonNull public String id = "";
  public String name = "";
  public String brand = "";
  public String type = "Deportivo";
  public int year;
  public String engine = "";
  public String transmission = "";
  public String power = "";
  public String traction = "";
  public String weight = "";
  public String image = "";
  public Long basePriceCents = 0L;

  public Vehicle() {}
}
