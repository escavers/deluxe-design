package com.deluxedesign.app.data.local;

import com.deluxedesign.app.data.OptionsCatalog;
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
        "1450 kg",
        "1890000"
      },
      {
        "bmw",
        "BMW M4 Competition",
        "BMW",
        "2024",
        "3.0 litros biturbo",
        "510 hp",
        "M xDrive",
        "1725 kg",
        "1960000"
      },
      {
        "mustang",
        "Mustang GT",
        "Ford",
        "2024",
        "5.0 litros V8",
        "480 hp",
        "Trasera (RWD)",
        "1750 kg",
        "1930000"
      },
      {
        "ferrari",
        "Ferrari 296 GTB",
        "Ferrari",
        "2024",
        "3.0 litros V6 híbrido",
        "830 hp",
        "Trasera (RWD)",
        "1470 kg",
        "2400000"
      },
      {
        "huracan",
        "Lamborghini Huracán",
        "Lamborghini",
        "2024",
        "5.2 litros V10",
        "640 hp",
        "Integral (AWD)",
        "1422 kg",
        "2300000"
      },
      {
        "mclaren",
        "McLaren 750S",
        "McLaren",
        "2024",
        "4.0 litros V8 biturbo",
        "740 hp",
        "Trasera (RWD)",
        "1277 kg",
        "2400000"
      },
      {
        "r8",
        "Audi R8 V10",
        "Audi",
        "2023",
        "5.2 litros V10",
        "620 hp",
        "Integral (AWD)",
        "1595 kg",
        "2200000"
      },
      {
        "corvette",
        "Corvette C8",
        "Chevrolet",
        "2024",
        "6.2 litros V8",
        "495 hp",
        "Trasera (RWD)",
        "1530 kg",
        "2000000"
      },
      {
        "gtr",
        "Nissan GT-R",
        "Nissan",
        "2024",
        "3.8 litros V6 biturbo",
        "570 hp",
        "Integral (AWD)",
        "1740 kg",
        "2100000"
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
      v.basePriceCents = Long.parseLong(row[8]);
      v.image = "ci_" + v.id + "_front34";
      if (v.id.equals("porsche")) v.type = "Deportivo";
      if (v.id.equals("bmw")) v.type = "Coupé";
      if (v.id.equals("mustang") || v.id.equals("corvette")) v.type = "Musculoso";
      list.add(v);
    }
    return list;
  }

  public static Vehicle vehicle(String id) {
    for (Vehicle v : vehicles()) if (v.id.equals(id)) return v;
    return null;
  }

  public static List<CustomizationPreset> presets() {
    List<CustomizationPreset> result = new ArrayList<>();
    for (Vehicle v : vehicles())
      for (String template : OptionsCatalog.TEMPLATES) {
        Map<String, String> opts = OptionsCatalog.templateOptions(template);
        CustomizationPreset p = new CustomizationPreset();
        p.id = v.id + "_" + template;
        p.vehicleId = v.id;
        p.name = OptionsCatalog.templateName(template);
        p.paint = OptionsCatalog.option(opts.get("paint")).label;
        p.finish = OptionsCatalog.option(opts.get("finish")).label;
        p.vinyl = OptionsCatalog.option(opts.get("vinyl")).label;
        p.wheels = OptionsCatalog.option(opts.get("wheels")).label;
        p.bodyKit = OptionsCatalog.option(opts.get("bodykit")).label;
        p.lights = OptionsCatalog.option(opts.get("lights")).label;
        p.accessories = OptionsCatalog.option(opts.get("accessories")).label;
        p.interior = OptionsCatalog.option(opts.get("interior")).label;
        long base = v.basePriceCents == null ? 0 : v.basePriceCents;
        for (String category : OptionsCatalog.CATEGORIES)
          base += OptionsCatalog.option(opts.get(category)).priceDeltaCents;
        p.priceCents = base;
        p.front = "ci_" + v.id + "_front34";
        p.side = "ci_" + v.id + "_side";
        p.rear = "ci_" + v.id + "_rear";
        result.add(p);
      }
    return result;
  }

  public static List<CustomizationOption> options() {
    return OptionsCatalog.options();
  }

  public static List<Branch> branches() {
    List<Branch> out = new ArrayList<>();
    String[][] rows = {
      {
        "central",
        "Deluxe Central - La Paz",
        "Av. Arce 2412, La Paz",
        "-16.513",
        "-68.124",
        "2 244 5566"
      },
      {
        "san_miguel",
        "Deluxe San Miguel",
        "Calle Montenegro, Zona Sur",
        "-16.542",
        "-68.080",
        "2 277 0810"
      },
      {
        "santa_cruz",
        "Deluxe Santa Cruz",
        "Av. San Martín, Equipetrol",
        "-17.765",
        "-63.197",
        "3 342 8899"
      }
    };
    for (String[] row : rows) {
      Branch b = new Branch();
      b.id = row[0];
      b.name = row[1];
      b.address = row[2];
      b.latitude = Double.parseDouble(row[3]);
      b.longitude = Double.parseDouble(row[4]);
      b.hours = "Lun–Sáb · 09:00–18:00";
      b.phone = row[5];
      out.add(b);
    }
    return out;
  }

  public static void install(DeluxeDao dao) {
    if (dao.vehicleCount() == 0) {
      for (Vehicle v : vehicles()) dao.put(v);
      for (CustomizationPreset p : presets()) dao.put(p);
      for (Branch b : branches()) dao.put(b);
    }
    if (dao.optionCount() == 0) for (CustomizationOption o : options()) dao.put(o);
    if (dao.user(DEMO_ID) != null) return;
    User u = new User();
    u.id = DEMO_ID;
    u.name = "Cliente Demo";
    u.email = "demo@deluxedesign.app";
    u.passwordHash = PasswordHasher.hash("Demo1234");
    dao.put(u);
    Favorite fav = new Favorite();
    fav.id = DEMO_ID + "_porsche";
    fav.userId = DEMO_ID;
    fav.vehicleId = "porsche";
    dao.put(fav);
    String[] statuses = {"Activo", "Entregado", "Borrador"};
    int[] progress = {45, 100, 0};
    String[] templates = {"urban_dark", "urban_dark", "street_blue"};
    for (int i = 0; i < 3; i++) {
      Vehicle v = vehicles().get(i);
      String template = templates[i];
      Map<String, String> opts = OptionsCatalog.templateOptions(template);
      CustomizationPreset preset = demoPreset(v, template, opts);
      Project p = new Project();
      p.id = "sample_" + v.id;
      p.userId = DEMO_ID;
      p.vehicleId = v.id;
      p.presetId = preset.id;
      p.name = v.name + " · " + preset.name;
      p.status = statuses[i];
      p.progress = progress[i];
      p.createdAt = System.currentTimeMillis() - i * 86400000L;
      p.estimatedDelivery = "Consultar con el taller";
      p.optionsJson = new Gson().toJson(opts.values());
      p.priceCents = preset.priceCents;
      dao.put(p);
      Quote q = new Quote();
      q.id = "DEMO-" + (101 + i);
      q.userId = DEMO_ID;
      q.projectId = p.id;
      q.customerName = u.name;
      q.totalCents = preset.priceCents;
      List<CustomizationOption> optionList = new ArrayList<>();
      for (String category : OptionsCatalog.CATEGORIES)
        optionList.add(OptionsCatalog.option(opts.get(category)));
      q.itemsJson = new Gson().toJson(QuoteCalculator.items(preset.priceCents, optionList));
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

  private static CustomizationPreset demoPreset(
      Vehicle v, String template, Map<String, String> opts) {
    CustomizationPreset p = new CustomizationPreset();
    p.id = v.id + "_" + template;
    p.vehicleId = v.id;
    p.name = OptionsCatalog.templateName(template);
    p.paint = OptionsCatalog.option(opts.get("paint")).label;
    p.finish = OptionsCatalog.option(opts.get("finish")).label;
    p.vinyl = OptionsCatalog.option(opts.get("vinyl")).label;
    p.wheels = OptionsCatalog.option(opts.get("wheels")).label;
    p.bodyKit = OptionsCatalog.option(opts.get("bodykit")).label;
    p.lights = OptionsCatalog.option(opts.get("lights")).label;
    p.accessories = OptionsCatalog.option(opts.get("accessories")).label;
    p.interior = OptionsCatalog.option(opts.get("interior")).label;
    long base = v.basePriceCents == null ? 0 : v.basePriceCents;
    for (String category : OptionsCatalog.CATEGORIES)
      base += OptionsCatalog.option(opts.get(category)).priceDeltaCents;
    p.priceCents = base;
    p.front = "ci_" + v.id + "_front34";
    p.side = "ci_" + v.id + "_side";
    p.rear = "ci_" + v.id + "_rear";
    return p;
  }
}