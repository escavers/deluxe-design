package com.deluxedesign.app.domain.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.util.List;

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

  @Ignore public transient List<CustomizationOption> options;

  public String image(String angle) {
    String value = "side".equals(angle) ? side : "rear".equals(angle) ? rear : front;
    return normalize(value, angle);
  }

  private static String normalize(String value, String angle) {
    if (value == null) return "";
    if (!value.startsWith("vehicle_")) return value;
    String s = value.replaceFirst("^vehicle_", "").replace("_reference", "");
    String suffix = "rear".equals(angle) ? "rear" : "side".equals(angle) ? "side" : "front34";
    if (s.endsWith("_front") || s.endsWith("_side") || s.endsWith("_rear"))
      s = s.substring(0, s.lastIndexOf('_'));
    return "ci_" + s + "_" + suffix;
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
