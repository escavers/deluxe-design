package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "quotes")
public class Quote {
  @PrimaryKey @NonNull public String id = "";
  public String userId = "";
  public String projectId = "";
  public String customerName = "";
  public String notes = "";
  public String status = "Pendiente";
  public String itemsJson = "[]";
  public long totalCents;
  public long createdAt;

  public Quote() {}
}
