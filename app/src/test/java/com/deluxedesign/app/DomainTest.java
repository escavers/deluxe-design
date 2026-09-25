package com.deluxedesign.app;

import static org.junit.Assert.*;

import com.deluxedesign.app.data.OptionsCatalog;
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
    int vehicles = SeedData.vehicles().size();
    assertTrue(vehicles >= 3);
    Set<String> ids = new HashSet<>();
    for (Vehicle v : SeedData.vehicles()) {
      int count = 0;
      for (CustomizationPreset p : SeedData.presets()) {
        assertFalse(p.summary().contains("null"));
        if (!p.vehicleId.equals(v.id)) continue;
        count++;
        assertTrue(ids.add(p.id));
        assertEquals("ci_" + v.id + "_front34", p.image("front"));
        assertEquals("ci_" + v.id + "_side", p.image("side"));
        assertEquals("ci_" + v.id + "_rear", p.image("rear"));
        assertTrue(p.priceCents > 0);
      }
      assertEquals(3, count);
    }
    assertEquals(vehicles * 3, ids.size());
  }

  @Test
  public void quoteBreakdownPreservesCents() {
    for (long amount : new long[] {0, 1, 99, 1890000, 1960000, 1930000})
      assertEquals(amount, QuoteCalculator.total(QuoteCalculator.items(amount, null)));
  }

  @Test
  public void catalogOptionsCoverAllCategoriesAndTemplates() {
    assertEquals(8, OptionsCatalog.CATEGORIES.length);
    Set<String> categories = new HashSet<>();
    Set<String> optionIds = new HashSet<>();
    for (CustomizationOption o : OptionsCatalog.options()) {
      categories.add(o.category);
      assertTrue("id duplicado: " + o.id, optionIds.add(o.id));
      assertTrue(o.priceDeltaCents != null && o.priceDeltaCents >= 0);
    }
    assertEquals(8, categories.size());
    for (String template : OptionsCatalog.TEMPLATES)
      assertEquals(8, OptionsCatalog.templateOptions(template).size());
    CustomizationOption racing = OptionsCatalog.option("paint_racing_red");
    assertEquals("Rojo Racing", racing.label);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeQuoteItemRejected() {
    QuoteCalculator.total(Arrays.asList(new QuoteItem("Incorrecto", -1)));
  }
}
