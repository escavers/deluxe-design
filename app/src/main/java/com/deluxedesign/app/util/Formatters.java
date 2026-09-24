package com.deluxedesign.app.util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class Formatters {
  public static String money(long cents) {
    return "$ " + NumberFormat.getNumberInstance(Locale.US).format(cents / 100.0);
  }

  public static String date(long time) {
    return new SimpleDateFormat("dd MMM yyyy", new Locale("es")).format(new Date(time));
  }
}
