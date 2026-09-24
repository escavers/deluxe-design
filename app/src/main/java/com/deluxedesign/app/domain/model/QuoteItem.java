package com.deluxedesign.app.domain.model;

public class QuoteItem {
  public String label;
  public long amountCents;

  public QuoteItem(String label, long amountCents) {
    this.label = label;
    this.amountCents = amountCents;
  }
}
