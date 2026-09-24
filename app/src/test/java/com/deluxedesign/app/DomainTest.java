package com.deluxedesign.app;

import static org.junit.Assert.*;

import com.deluxedesign.app.data.local.SeedData;
import com.deluxedesign.app.domain.model.*;
import com.deluxedesign.app.util.*;
import java.util.*;
import org.junit.Test;

public class DomainTest {
  @Test
  public void validatesCredentials() {
    assertTrue(Validators.email("cliente@example.com"));
    assertFalse(Validators.email("sin-correo"));
    assertFalse(Validators.email("a b@c.com"));
    assertEquals("cliente@example.com", Validators.normalizeEmail(" Cliente@Example.com "));
    assertFalse(Validators.password("1234567"));
    assertTrue(Validators.password("Demo1234"));
  }

  @Test
  public void passwordsAreSaltedAndVerified() {
    String a = PasswordHasher.hash("Demo1234"), b = PasswordHasher.hash("Demo1234");
    assertNotEquals(a, b);
    assertTrue(PasswordHasher.verify("Demo1234", a));
    assertFalse(PasswordHasher.verify("incorrecta", a));
    assertFalse(PasswordHasher.verify("Demo1234", "corrupto"));
  }

  @Test
  public void exactlyThreePresetsPerVehicleWithConsistentResources() {
    assertEquals(3, SeedData.vehicles().size());
    Set<String> ids = new HashSet<>();
    for (Vehicle v : SeedData.vehicles()) {
      int count = 0;
      for (CustomizationPreset p : SeedData.presets()) {
        assertFalse(p.summary().contains("null"));
        if (!p.vehicleId.equals(v.id)) continue;
        count++;
        assertTrue(ids.add(p.id));
        assertEquals("vehicle_" + p.id + "_front", p.image("front"));
        assertEquals("vehicle_" + p.id + "_side", p.image("side"));
        assertEquals("vehicle_" + p.id + "_rear", p.image("rear"));
        assertTrue(p.priceCents > 0);
      }
      assertEquals(3, count);
    }
    assertEquals(9, ids.size());
  }

  @Test
  public void quoteBreakdownPreservesCents() {
    for (long amount : new long[] {0, 1, 99, 1890000, 2140000, 2390001})
      assertEquals(amount, QuoteCalculator.total(QuoteCalculator.items(amount)));
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeQuoteItemRejected() {
    QuoteCalculator.total(Arrays.asList(new QuoteItem("Incorrecto", -1)));
  }
}
