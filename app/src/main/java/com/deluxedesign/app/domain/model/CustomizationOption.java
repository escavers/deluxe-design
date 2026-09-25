package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "options")
public class CustomizationOption {
  @PrimaryKey @NonNull public String id = "";
  public String category = "";
  public String label = "";
  public String detail = "";
  public Long priceDeltaCents = 0L;
  public String swatch = "";

  public CustomizationOption() {}
}