/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.common.TimeSource;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.sys.PredictiveInventory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/** ultimately, billing will replicate to every caller and the billing system has to dedupe by id */
public class MeteringPubSub {
  private final TimeSource time;
  private final DeploymentFactoryBase base;
  private final ArrayList<Function<ArrayList<MeterReading>, Boolean>> subscribers;
  private ArrayList<MeterReading> lastValue;
  private long lastPublish;

  public MeteringPubSub(TimeSource time, DeploymentFactoryBase base) {
    this.time = time;
    this.base = base;
    this.subscribers = new ArrayList<>();
    this.lastValue = null;
    this.lastPublish = time.nowMilliseconds();
  }

  public synchronized void subscribe(Function<ArrayList<MeterReading>, Boolean> subscriber) {
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

  public Consumer<HashMap<String, PredictiveInventory.MeteringSample>> publisher() {
    return samples -> {
      long now = time.nowMilliseconds();
      long delta = now - lastPublish;
      lastPublish = now;
      ArrayList<MeterReading> meterReading = new ArrayList<>();
      for (Map.Entry<String, PredictiveInventory.MeteringSample> sample : samples.entrySet()) {
        String space = sample.getKey();
        String hash = base.hashOf(space);
        if (hash != null) {
          meterReading.add(new MeterReading(time.nowMilliseconds(), delta, space, hash, sample.getValue()));
        }
      }
      publish(meterReading);
    };
  }

  private synchronized void publish(ArrayList<MeterReading> meterReading) {
    Iterator<Function<ArrayList<MeterReading>, Boolean>> it = subscribers.iterator();
    lastValue = meterReading;
    while (it.hasNext()) {
      if (!it.next().apply(meterReading)) {
        it.remove();
      }
    }
  }

}
