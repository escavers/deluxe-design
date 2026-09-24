package com.deluxedesign.app.util;

import com.deluxedesign.app.domain.model.QuoteItem;
import java.util.ArrayList;
import java.util.List;

public final class QuoteCalculator {
  public static List<QuoteItem> items(long price) {
    List<QuoteItem> result = new ArrayList<>();
    long paint = price * 40 / 100, vinyl = price * 20 / 100, wheels = price * 25 / 100;
    result.add(new QuoteItem("Pintura y acabado", paint));
    result.add(new QuoteItem("Vinilos y accesorios", vinyl));
    result.add(new QuoteItem("Llantas y body kit", wheels));
    result.add(new QuoteItem("Mano de obra", price - paint - vinyl - wheels));
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
}
