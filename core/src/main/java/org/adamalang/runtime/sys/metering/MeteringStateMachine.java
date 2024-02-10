/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
      accum.put(space, new PredictiveInventory.MeteringSample(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));
    }
    return this;
  }
}
