package org.adamalang.runtime.sys.billing;

import org.adamalang.runtime.sys.PredictiveInventory;

import java.util.UUID;

/** a billing for a space */
public class Bill {
  public final String id;
  public final String space;
  public final String hash;

  public final long memory;
  public final long cpu;
  public final long count;
  public final long messages;

  public Bill(String space, String hash, PredictiveInventory.Billing billing) {
    this.id = UUID.randomUUID().toString();
    this.space = space;
    this.hash = hash;
    this.memory = billing.memory;
    this.cpu = billing.cpu;
    this.count = billing.count;
    this.messages = billing.messages;
  }
}
