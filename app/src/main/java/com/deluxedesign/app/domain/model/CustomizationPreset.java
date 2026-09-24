package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "presets")
public class CustomizationPreset {
  @PrimaryKey @NonNull public String id = "";
  public String vehicleId = "";
  public String name = "";
  public String front = "";
  public String side = "";
  public String rear = "";
  public String paint = "";
  public String finish = "";
  public String vinyl = "";
  public String wheels = "";
  public String bodyKit = "";
  public String lights = "";
  public String accessories = "";
  public String interior = "";
  public long priceCents;

  public String image(String angle) {
    return "side".equals(angle) ? side : "rear".equals(angle) ? rear : front;
  }

  public String summary() {
    return "Pintura: "
        + paint
        + " · "
        + finish
        + "\nVinilos: "
        + vinyl
        + "\nLlantas: "
        + wheels
        + "\nBody kit: "
        + bodyKit
        + "\nFaros: "
        + lights
        + "\nAccesorios: "
        + accessories
        + "\nInteriores: "
        + interior;
  }

  public CustomizationPreset() {}
}
