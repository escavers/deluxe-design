package com.deluxedesign.app.util;

import com.deluxedesign.app.domain.model.CustomizationOption;
import com.deluxedesign.app.domain.model.QuoteItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QuoteCalculator {
  public static String categoryLabel(String category) {
    if ("paint".equals(category)) return "Color";
    if ("wheels".equals(category)) return "Llantas";
    if ("lights".equals(category)) return "Faros";
    if ("spoiler".equals(category)) return "Alerón";
    if ("interior".equals(category)) return "Interiores";
    return "Personalización";
  }

  /**
   * Desglosa una cotización: base del vehículo + cada opción elegida (delta) + mano de obra. Si
   * options es null se usa el desglose porcentual histórico.
   */
  public static List<QuoteItem> items(long baseCents, List<CustomizationOption> options) {
    List<QuoteItem> result = new ArrayList<>();
    if (options == null) {
      long paint = baseCents * 40 / 100, vinyl = baseCents * 20 / 100, wheels = baseCents * 25 / 100;
      result.add(new QuoteItem("Pintura y acabado", paint));
      result.add(new QuoteItem("Vinilos y accesorios", vinyl));
      result.add(new QuoteItem("Llantas y body kit", wheels));
      result.add(new QuoteItem("Mano de obra", baseCents - paint - vinyl - wheels));
      return result;
    }
    long extras = 0;
    result.add(new QuoteItem("Base del vehículo", baseCents));
    for (CustomizationOption option : options) {
      if (option == null || option.priceDeltaCents == null || option.priceDeltaCents <= 0) continue;
      result.add(
          new QuoteItem(
              OptionsLabel.sentence(option), option.priceDeltaCents));
      extras += option.priceDeltaCents;
    }
    long labor = Math.round((baseCents + extras) * 0.08);
    result.add(new QuoteItem("Mano de obra", labor));
    return result;
  }

  public static long total(List<QuoteItem> items) {
    long sum = 0;
    for (QuoteItem item : items) {
      if (item.amountCents < 0) throw new IllegalArgumentException("Costo negativo");
      sum = Math.addExact(sum, item.amountCents);
    }
    return sum;
  }

  private static final class OptionsLabel {
    static String sentence(CustomizationOption option) {
      String cat = categoryLabel(option.category);
      return (cat + ": " + option.label).toLowerCase(Locale.ROOT);
    }
  }
}