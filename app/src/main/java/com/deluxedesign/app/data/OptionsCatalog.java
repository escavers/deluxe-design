package com.deluxedesign.app.data;

import com.deluxedesign.app.domain.model.CustomizationOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Catálogo único de opciones de personalización y de las plantillas predefinidas. SeedData y el
 * configurador comparten estas constantes para que los ids de opción y las plantillas coincidan en
 * local y en la nube.
 */
public final class OptionsCatalog {
  public static final String[] CATEGORIES =
      {"paint", "finish", "vinyl", "wheels", "bodykit", "lights", "accessories", "interior"};

  public static final String TEMPLATE_RACING = "racing_red";
  public static final String TEMPLATE_URBAN = "urban_dark";
  public static final String TEMPLATE_STREET = "street_blue";
  public static final String[] TEMPLATES = {TEMPLATE_RACING, TEMPLATE_URBAN, TEMPLATE_STREET};

  public static List<CustomizationOption> options() {
    List<CustomizationOption> list = new ArrayList<>();
    String[][] rows = {
      // id, category, label, detail, priceDeltaCents, swatch
      {"paint_racing_red", "paint", "Rojo Racing", "Rojo intenso de competencia", "0", "#E23B3B"},
      {"paint_urban_black", "paint", "Negro Urban", "Negro mate profundo", "250000", "#17181C"},
      {"paint_street_blue", "paint", "Azul Street", "Azul eléctrico urbano", "500000", "#2A6FE0"},
      {"paint_devil_green", "paint", "Verde Devil", "Verde esmeralda metalizado", "450000", "#0E7A3C"},
      {"paint_racing_yellow", "paint", "Amarillo Racing", "Amarillo de circuito", "300000", "#F2C200"},
      {"paint_pearl_white", "paint", "Blanco Perla", "Blanco nacarado", "350000", "#F2F2F4"},
      {"finish_metallic", "finish", "Metálico", "Brillo con destellos", "0", ""},
      {"finish_matte", "finish", "Mate", "Sin brillo, deportivo", "180000", ""},
      {"finish_pearl", "finish", "Perlado", "Acabado nacarado", "220000", ""},
      {"finish_carbon", "finish", "Carbono", "Textura de fibra visible", "260000", ""},
      {"vinyl_stripes", "vinyl", "Franjas Racing", "Dos franjas longitudinales", "0", ""},
      {"vinyl_dark_sides", "vinyl", "Laterales Dark", "Vinilo oscuro en laterales", "150000", ""},
      {"vinyl_street_lines", "vinyl", "Líneas Street", "Líneas laterales finas", "190000", ""},
      {"vinyl_double_stripe", "vinyl", "Doble franja negra", "Franjas deportivas negras", "120000", ""},
      {"wheels_sport19", "wheels", "Deportivas 19\"", "Rines de aleación deportivos", "0", "#B9BCC4"},
      {"wheels_black20", "wheels", "Negras 20\"", "Rines oscurecidos 20 pulgadas", "120000", "#1B1C20"},
      {"wheels_silver19", "wheels", "Plata 19\"", "Rines plata pulidos", "95000", "#E5E7EB"},
      {"wheels_chrome21", "wheels", "Cromo 21\"", "Rines cromados 21 pulgadas", "260000", "#D7D9DF"},
      {"bodykit_wide", "bodykit", "Wide Body", "Ensanche de pasos de rueda", "0", ""},
      {"bodykit_urban_aero", "bodykit", "Urban Aero", "Faldones aerodinámicos urbanos", "140000", ""},
      {"bodykit_street_sport", "bodykit", "Street Sport", "Paquete deportivo de calle", "110000", ""},
      {"bodykit_track_cup", "bodykit", "Racing Cup", "Kit de competición completo", "320000", ""},
      {"lights_led_white", "lights", "LED blanco", "Faros LED blancos", "0", "#FFFFFF"},
      {"lights_led_blue", "lights", "LED azul", "Faros LED azulados", "60000", "#7DC4FF"},
      {"lights_matrix", "lights", "Matrix LED", "Matriz adaptativa", "120000", "#E8F6FF"},
      {"acc_wing", "accessories", "Alerón deportivo", "Alerón trasero fijo", "0", ""},
      {"acc_diffuser", "accessories", "Difusor negro", "Difusor trasero oscuro", "90000", ""},
      {"acc_splitter", "accessories", "Splitter frontal", "Splitter delantero", "70000", ""},
      {"acc_track_pack", "accessories", "Paquete Track", "Alerón + splitter + difusor", "240000", ""},
      {"interior_red_black", "interior", "Cuero negro / rojo", "Asientos de cuero con costuras rojas", "0", "#8A1C1C"},
      {"interior_alcantara", "interior", "Alcántara negra", "Tapicería Alcántara sin brillo", "160000", "#2A2B30"},
      {"interior_blue_black", "interior", "Cuero negro / azul", "Cuero con costuras azules", "140000", "#1E3A66"},
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

  /** Opciones completas por plantilla (ordenadas por categoría). */
  public static Map<String, String> templateOptions(String template) {
    Map<String, String> map = new LinkedHashMap<>();
    String[] keys;
    if (TEMPLATE_RACING.equals(template))
      keys =
          new String[] {
            "paint_racing_red", "finish_metallic", "vinyl_stripes", "wheels_sport19",
            "bodykit_wide", "lights_led_white", "acc_wing", "interior_red_black"
          };
    else if (TEMPLATE_URBAN.equals(template))
      keys =
          new String[] {
            "paint_urban_black", "finish_matte", "vinyl_dark_sides", "wheels_black20",
            "bodykit_urban_aero", "lights_led_white", "acc_diffuser", "interior_alcantara"
          };
    else
      keys =
          new String[] {
            "paint_street_blue", "finish_pearl", "vinyl_street_lines", "wheels_silver19",
            "bodykit_street_sport", "lights_led_white", "acc_splitter", "interior_blue_black"
          };
    for (int i = 0; i < CATEGORIES.length && i < keys.length; i++)
      map.put(CATEGORIES[i], keys[i]);
    return map;
  }

  public static String templateName(String template) {
    if (TEMPLATE_URBAN.equals(template)) return "Urban Dark";
    if (TEMPLATE_STREET.equals(template)) return "Street Blue";
    return "Racing Red";
  }

  public static boolean isTemplate(String id) {
    return Arrays.asList(TEMPLATES).contains(id);
  }

  /** Devuelve la plantilla cuyo conjunto coincide con las selecciones, o "custom". */
  public static String templateFor(Map<String, String> selections) {
    for (String template : TEMPLATES) {
      Map<String, String> t = templateOptions(template);
      boolean same = true;
      for (String category : CATEGORIES)
        if (!t.get(category).equals(selections.get(category))) {
          same = false;
          break;
        }
      if (same) return template;
    }
    return "custom";
  }

  private OptionsCatalog() {}
}