package com.deluxedesign.app.util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Local demo credentials are salted; Firebase handles credentials in cloud mode. */
public final class PasswordHasher {
  private PasswordHasher() {}

  public static String hash(String password) {
    byte[] salt = new byte[16];
    new SecureRandom().nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt)
        + ":"
        + Base64.getEncoder().encodeToString(derive(password, salt));
  }

  public static boolean verify(String password, String encoded) {
    try {
      String[] p = encoded.split(":");
      return MessageDigest.isEqual(
          Base64.getDecoder().decode(p[1]), derive(password, Base64.getDecoder().decode(p[0])));
    } catch (Exception e) {
      return false;
    }
  }

  private static byte[] derive(String value, byte[] salt) {
    try {
      PBEKeySpec spec = new PBEKeySpec(value.toCharArray(), salt, 60000, 256);
      byte[] result =
          SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
      spec.clearPassword();
      return result;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
