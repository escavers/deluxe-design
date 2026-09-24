package com.deluxedesign.app.data.local;

import com.deluxedesign.app.domain.model.*;
import com.deluxedesign.app.util.*;
import com.google.gson.Gson;
import java.util.*;

public final class SeedData {
  public static final String DEMO_ID = "demo";

  public static List<Vehicle> vehicles() {
    List<Vehicle> list = new ArrayList<>();
    String[][] values = {
      {
        "porsche",
        "Porsche GT3 RS",
        "Porsche",
        "2024",
        "4.0 litros atmosférico",
        "518 hp",
        "Trasera (RWD)",
        "1450 kg"
      },
      {
        "bmw",
        "BMW M4 Competition",
        "BMW",
        "2024",
        "3.0 litros biturbo",
        "510 hp",
        "M xDrive",
        "1725 kg"
      },
      {
        "mustang",
        "Mustang GT",
        "Ford",
        "2024",
        "5.0 litros V8",
        "480 hp",
        "Trasera (RWD)",
        "1750 kg"
      }
    };
    for (String[] row : values) {
      Vehicle v = new Vehicle();
      v.id = row[0];
      v.name = row[1];
      v.brand = row[2];
      v.year = Integer.parseInt(row[3]);
      v.engine = row[4];
      v.power = row[5];
      v.traction = row[6];
      v.weight = row[7];
      v.transmission = "Automática";
      v.image = "vehicle_" + v.id + "_racing_red_front";
      list.add(v);
    }
    return list;
  }

  public static List<CustomizationPreset> presets() {
    List<CustomizationPreset> result = new ArrayList<>();
    String[] codes = {"racing_red", "urban_dark", "street_blue"},
        names = {"Racing Red", "Urban Dark", "Street Blue"};
    for (Vehicle v : vehicles())
      for (int i = 0; i < 3; i++) {
        CustomizationPreset p = new CustomizationPreset();
        p.id = v.id + "_" + codes[i];
        p.vehicleId = v.id;
        p.name = names[i];
        p.front = "vehicle_" + p.id + "_front";
        p.side = "vehicle_" + p.id + "_side";
        p.rear = "vehicle_" + p.id + "_rear";
        p.paint = new String[] {"Rojo Racing", "Negro Urban", "Azul Street"}[i];
        p.finish = new String[] {"Metálico", "Mate", "Perlado"}[i];
        p.vinyl = new String[] {"Franjas Racing", "Laterales Dark", "Líneas Street"}[i];
        p.wheels =
            new String[] {"Deportivas 19 pulgadas", "Negras 20 pulgadas", "Plata 19 pulgadas"}[i];
        p.bodyKit = new String[] {"Wide Body", "Urban Aero", "Street Sport"}[i];
        p.lights = "LED blanco";
        p.accessories = new String[] {"Alerón deportivo", "Difusor negro", "Splitter frontal"}[i];
        p.interior =
            new String[] {"Cuero negro / rojo", "Alcántara negra", "Cuero negro / azul"}[i];
        p.priceCents = 1890000L + i * 250000;
        result.add(p);
      }
    return result;
  }

  public static List<Branch> branches() {
    List<Branch> out = new ArrayList<>();
    String[][] rows = {
      {"central", "Deluxe Central - La Paz", "Av. Arce 2412, La Paz", "-16.513", "-68.124"},
      {"san_miguel", "Deluxe San Miguel", "Calle Montenegro, Zona Sur", "-16.542", "-68.080"},
      {"santa_cruz", "Deluxe Santa Cruz", "Av. San Martín, Equipetrol", "-17.765", "-63.197"}
    };
    for (String[] row : rows) {
      Branch b = new Branch();
      b.id = row[0];
      b.name = row[1];
      b.address = row[2];
      b.latitude = Double.parseDouble(row[3]);
      b.longitude = Double.parseDouble(row[4]);
      b.hours = "Lun–Sáb · 09:00–18:00";
      b.phone = "Datos de demostración";
      out.add(b);
    }
    return out;
  }

  public static void install(DeluxeDao dao) {
    if (dao.vehicleCount() != 0) return;
    for (Vehicle v : vehicles()) dao.put(v);
    for (CustomizationPreset p : presets()) dao.put(p);
    for (Branch b : branches()) dao.put(b);
    User u = new User();
    u.id = DEMO_ID;
    u.name = "Cliente Demo";
    u.email = "demo@deluxedesign.app";
    u.passwordHash = PasswordHasher.hash("Demo1234");
    dao.put(u);
    String[] statuses = {"Activo", "Entregado", "Borrador"};
    int[] progress = {45, 100, 0};
    for (int i = 0; i < 3; i++) {
      Vehicle v = vehicles().get(i);
      Project p = new Project();
      p.id = "sample_" + v.id;
      p.userId = DEMO_ID;
      p.vehicleId = v.id;
      p.presetId = v.id + "_urban_dark";
      p.name = v.name + " · Urban Dark";
      p.status = statuses[i];
      p.progress = progress[i];
      p.createdAt = System.currentTimeMillis() - i * 86400000L;
      p.estimatedDelivery = "Consultar con el taller";
      dao.put(p);
      Quote q = new Quote();
      q.id = "DEMO-" + (101 + i);
      q.userId = DEMO_ID;
      q.projectId = p.id;
      q.customerName = u.name;
      q.totalCents = 2140000;
      q.itemsJson = new Gson().toJson(QuoteCalculator.items(q.totalCents));
      q.status = i == 1 ? "Aprobada" : i == 0 ? "En revisión" : "Pendiente";
      q.createdAt = p.createdAt;
      dao.put(q);
      NotificationItem n = new NotificationItem();
      n.id = "notice_" + i;
      n.userId = DEMO_ID;
      n.title =
          new String[] {"Proyecto en proceso", "Cotización aprobada", "Nuevo modelo disponible"}[i];
      n.message =
          new String[] {
                "Tu BMW M4 avanza en el taller.",
                "Revisa el detalle de tu presupuesto.",
                "Mustang GT ya está en el catálogo."
              }
              [i];
      n.createdAt = p.createdAt;
      dao.put(n);
    }
  }
}
