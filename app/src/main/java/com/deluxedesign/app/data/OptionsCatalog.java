package com.deluxedesign.app.data;

import com.deluxedesign.app.domain.model.CustomizationOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catálogo único de opciones de personalización. La categoría paint se corresponde con los cinco
 * colores de la carpeta de assets vehicles/ (rojo, azul, blanco, negro y plomo).
 */
public final class OptionsCatalog {
  public static final String[] CATEGORIES = {"paint", "wheels", "lights", "spoiler", "interior"};

  public static final String TEMPLATE_RACING = "racing_red";
  public static final String TEMPLATE_URBAN = "urban_dark";
  public static final String TEMPLATE_STREET = "street_blue";
  public static final String[] TEMPLATES = {TEMPLATE_RACING, TEMPLATE_URBAN, TEMPLATE_STREET};

  public static List<CustomizationOption> options() {
    List<CustomizationOption> list = new ArrayList<>();
    String[][] rows = {
      // Orden de columnas (nombres de campo de CustomizationOption):
      // id, category, label, detail, priceDeltaCents, swatch
      {"paint_rojo", "paint", "Rojo", "Rojo intenso", "0", "#E23B3B"},
      {"paint_azul", "paint", "Azul", "Azul eléctrico", "0", "#2A6FE0"},
      {"paint_blanco", "paint", "Blanco", "Blanco perla", "0", "#F2F2F4"},
      {"paint_negro", "paint", "Negro", "Negro profundo", "0", "#17181C"},
      {"paint_plomo", "paint", "Plomo", "Gris plomo metalizado", "0", "#6B7280"},
      {"wheels_sport19", "wheels", "Deportivas 19\"", "Rines de aleación deportivos", "0", "#B9BCC4"},
      {"wheels_black20", "wheels", "Negras 20\"", "Rines oscurecidos 20 pulgadas", "120000", "#1B1C20"},
      {"wheels_silver19", "wheels", "Plata 19\"", "Rines plata pulidos", "95000", "#E5E7EB"},
      {"wheels_chrome21", "wheels", "Cromo 21\"", "Rines cromados 21 pulgadas", "260000", "#D7D9DF"},
      {"lights_led_white", "lights", "LED blanco", "Faros LED blancos", "0", "#FFFFFF"},
      {"lights_led_blue", "lights", "LED azul", "Faros LED azulados", "60000", "#7DC4FF"},
      {"lights_matrix", "lights", "Matrix LED", "Matriz adaptativa", "120000", "#E8F6FF"},
      {"spoiler_deportivo", "spoiler", "Alerón deportivo", "Alerón trasero fijo", "0", "#3A3D46"},
      {"spoiler_gt", "spoiler", "Alerón GT", "Alerón elevado de competición", "150000", "#27292F"},
      {"spoiler_carbon", "spoiler", "Alerón de carbono", "Alerón de fibra de carbono", "320000", "#23252B"},
      {"spoiler_dual", "spoiler", "Doble alerón", "Kit de doble alerón trasero", "400000", "#1B1C20"},
      {"interior_red_black", "interior", "Cuero negro / rojo", "Asientos de cuero con costuras rojas", "0", "#8A1C1C"},
      {"interior_blue_black", "interior", "Cuero negro / azul", "Cuero con costuras azules", "140000", "#1E3A66"},
      {"interior_alcantara", "interior", "Alcántara negra", "Tapicería Alcántara sin brillo", "160000", "#2A2B30"},
      {"interior_carbon", "interior", "Carbono deportivo", "Insertos de carbono ligero", "380000", "#23252B"}
    };
    for (String[] row : rows) {
      CustomizationOption o = new CustomizationOption();
      o.id = row[0];
      o.category = row[1];
      o.label = row[2];
      o.detail = row[3];
      o.priceDeltaCents = Long.parseLong(row[4]);
      o.swatch = row[5];
      list.add(o);
    }
    return list;
  }

  public static CustomizationOption option(String id) {
    for (CustomizationOption o : options()) if (o.id.equals(id)) return o;
    return null;
  }

  public static List<CustomizationOption> forCategory(String category) {
    List<CustomizationOption> list = new ArrayList<>();
    for (CustomizationOption o : options()) if (o.category.equals(category)) list.add(o);
    return list;
  }

  /** Selección por defecto: el primer color y la opción base de cada categoría. */
  public static Map<String, String> defaultOptions() {
    Map<String, String> map = new LinkedHashMap<>();
    map.put("paint", "paint_rojo");
    map.put("wheels", "wheels_sport19");
    map.put("lights", "lights_led_white");
    map.put("spoiler", "spoiler_deportivo");
    map.put("interior", "interior_red_black");
    return map;
  }

  /** Compatibilidad con presets antiguos: cualquier plantilla cae a las opciones por defecto. */
  public static Map<String, String> templateOptions(String template) {
    return defaultOptions();
  }

  public static String templateName(String template) {
    return "Estándar";
  }

  public static boolean isTemplate(String id) {
    return Arrays.asList(TEMPLATES).contains(id);
  }

  public static String templateFor(Map<String, String> selections) {
    return "custom";
  }

  private OptionsCatalog() {}
}