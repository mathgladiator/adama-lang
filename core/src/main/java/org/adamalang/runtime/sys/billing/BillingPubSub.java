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

import org.adamalang.common.TimeSource;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.PredictiveInventory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/** ultimately, billing will replicate to every caller and the billing system has to dedupe by id */
public class BillingPubSub {
  private final TimeSource time;
  private final DeploymentFactoryBase base;
  private final ArrayList<Function<ArrayList<Bill>, Boolean>> subscribers;
  private ArrayList<Bill> lastValue;
  private long lastPublish;

  public BillingPubSub(TimeSource time, DeploymentFactoryBase base) {
    this.time = time;
    this.base = base;
    this.subscribers = new ArrayList<>();
    this.lastValue = null;
    this.lastPublish = time.nowMilliseconds();
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

  public Consumer<HashMap<String, PredictiveInventory.Billing>> publisher() {
    return billings -> {
      long now = time.nowMilliseconds();
      long delta = now - lastPublish;
      lastPublish = now;
      ArrayList<Bill> bill = new ArrayList<>();
      for (Map.Entry<String, PredictiveInventory.Billing> billing : billings.entrySet()) {
        String space = billing.getKey();
        String hash = base.hashOf(space);
        if (hash != null) {
          bill.add(new Bill(time.nowMilliseconds(), delta, space, hash, billing.getValue()));
        }
      }
      publish(bill);
    };
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

}
