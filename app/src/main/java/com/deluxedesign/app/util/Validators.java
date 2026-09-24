package com.deluxedesign.app.util;

import java.util.Locale;

public final class Validators {
  private Validators() {}

  public static String normalizeEmail(String value) {
    return value.trim().toLowerCase(Locale.ROOT);
  }

  public static boolean email(String value) {
    return value != null && value.trim().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
  }

  public static boolean password(String value) {
    return value != null && value.length() >= 8;
  }

  public static String credentials(String email, String password) {
    if (!email(email)) return "Ingresa un correo válido.";
    if (!password(password)) return "La contraseña debe tener al menos 8 caracteres.";
    return null;
  }
}
