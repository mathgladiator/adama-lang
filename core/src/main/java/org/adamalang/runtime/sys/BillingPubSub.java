/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys;

import org.adamalang.runtime.deploy.DeploymentFactoryBase;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/** ultimately, billing will replicate to every caller and the billing system has to dedupe by id */
public class BillingPubSub {
  private final DeploymentFactoryBase base;
  private final ArrayList<Function<ArrayList<Bill>, Boolean>> subscribers;
  private ArrayList<Bill> lastValue;

  public BillingPubSub(DeploymentFactoryBase base) {
    this.base = base;
    this.subscribers = new ArrayList<>();
    this.lastValue = null;
  }

  public static class Bill {
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

  public synchronized void subscribe(Function<ArrayList<Bill>, Boolean> subscriber) {
    if (lastValue != null) {
      if (subscriber.apply(lastValue)) {
        subscribers.add(subscriber);
      }
    } else {
      subscribers.add(subscriber);
    }
  }

  public synchronized int size() {
    return subscribers.size();
  }

  private synchronized void publish(ArrayList<Bill> bill) {
    Iterator<Function<ArrayList<Bill>, Boolean>> it = subscribers.iterator();
    lastValue = bill;
    while (it.hasNext()) {
      if (!it.next().apply(bill)) {
        it.remove();
      }
    }
  }

  public Consumer<HashMap<String, PredictiveInventory.Billing>> publisher() {
    return billings -> {
      ArrayList<Bill> bill = new ArrayList<>();
      for (Map.Entry<String, PredictiveInventory.Billing> billing : billings.entrySet()) {
        String space = billing.getKey();
        String hash = base.hashOf(space);
        if (hash != null) {
          bill.add(new Bill(space, hash, billing.getValue()));
        }
      }
      publish(bill);
    };
  }
}
