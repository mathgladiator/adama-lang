/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
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
