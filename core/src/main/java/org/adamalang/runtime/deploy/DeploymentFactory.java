/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.keys.PrivateKeyBundle;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.translator.jvm.LivingDocumentFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * converts a DeploymentPlan into a LivingDocumentFactoryFactory; if this can be created, then it is
 * in good order
 */
public class DeploymentFactory implements LivingDocumentFactoryFactory {
  public final String name;
  public final DeploymentPlan plan;
  public final long memoryUsed;
  public final HashMap<String, LivingDocumentFactory> factories;

  public DeploymentFactory(String name, DeploymentPlan plan, long memoryUsed, HashMap<String, LivingDocumentFactory> factories) {
    this.name = name;
    this.plan = plan;
    this.memoryUsed = memoryUsed;
    this.factories = factories;
  }

  @Override
  public void fetch(Key key, Callback<LivingDocumentFactory> callback) {
    String versionToUse = plan.pickVersion(key.key);
    callback.success(factories.get(versionToUse));
  }

  @Override
  public void account(HashMap<String, PredictiveInventory.MeteringSample> sample) {
    PredictiveInventory.MeteringSample prior = sample.get(name);
    if (prior == null) {
      sample.put(name, PredictiveInventory.MeteringSample.justMemory(memoryUsed));
    } else {
      sample.put(name, PredictiveInventory.MeteringSample.add(prior, PredictiveInventory.MeteringSample.justMemory(memoryUsed)));
    }
  }

  @Override
  public Collection<String> spacesAvailable() {
    return Collections.singleton(name);
  }
}
