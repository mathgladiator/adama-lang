/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.DocumentThreadBase;
import org.adamalang.runtime.sys.PredictiveInventory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** a state machine for computing a service meter across all threads */
public class MeteringStateMachine {
  private final DocumentThreadBase[] bases;
  private final Consumer<HashMap<String, PredictiveInventory.MeteringSample>> onFinalSampling;
  private final HashMap<String, PredictiveInventory.MeteringSample> accum;
  private int at;

  private MeteringStateMachine(DocumentThreadBase[] bases, Consumer<HashMap<String, PredictiveInventory.MeteringSample>> onFinalSampling) {
    this.bases = bases;
    this.at = 0;
    this.onFinalSampling = onFinalSampling;
    this.accum = new HashMap<>();
  }

  public static void estimate(DocumentThreadBase[] bases, LivingDocumentFactoryFactory factory, Consumer<HashMap<String, PredictiveInventory.MeteringSample>> onFinalSampling) {
    new MeteringStateMachine(bases, onFinalSampling).seed(factory.spacesAvailable()).next();
  }

  private void next() {
    if (at < bases.length) {
      bases[at].sampleMetering((b) -> {
        for (Map.Entry<String, PredictiveInventory.MeteringSample> entry : b.entrySet()) {
          PredictiveInventory.MeteringSample prior = accum.get(entry.getKey());
          if (prior != null) {
            accum.put(entry.getKey(), PredictiveInventory.MeteringSample.add(prior, entry.getValue()));
          } else {
            accum.put(entry.getKey(), entry.getValue());
          }
        }
        at++;
        next();
      });
    } else {
      onFinalSampling.accept(accum);
    }
  }

  private MeteringStateMachine seed(Collection<String> spaces) {
    for (String space : spaces) {
      accum.put(space, new PredictiveInventory.MeteringSample(0, 0, 0, 0, 0, 0, 0, 0));
    }
    return this;
  }
}
