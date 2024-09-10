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
package org.adamalang.runtime.sys.readonly;

import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.runtime.sys.ServiceShield;

import java.util.HashMap;

/** we create a readonly version of the entire system for simplicity sake */
public class ReadOnlyReplicaThreadBase {
  public final int threadId;
  public final ServiceShield shield;
  public final CoreMetrics metrics;
  public final SimpleExecutor executor;
  public final HashMap<Key, ReadOnlyLivingDocument> map;
  private final HashMap<String, PredictiveInventory> inventoryBySpace;
  private final LivingDocumentFactoryFactory livingDocumentFactoryFactory;
  private final ReplicationInitiator initiator;

  public ReadOnlyReplicaThreadBase(int threadId, ServiceShield shield, CoreMetrics metrics, LivingDocumentFactoryFactory livingDocumentFactoryFactory, ReplicationInitiator initiator, SimpleExecutor executor) {
    this.threadId = threadId;
    this.shield = shield;
    this.metrics = metrics;
    this.executor = executor;
    this.livingDocumentFactoryFactory = livingDocumentFactoryFactory;
    this.initiator = initiator;
    this.map = new HashMap<>();
    this.inventoryBySpace = new HashMap<>();
  }

  public void observe(CoreRequestContext context, Key key, ReadOnlyStream stream) {
    executor.execute(new NamedRunnable("find-document") {
      @Override
      public void execute() throws Exception {
        ReadOnlyLivingDocument document = map.get(key);
        if (document != null) {

        }
      }
    });
  }

  public PredictiveInventory getOrCreateInventory(String space) {
    PredictiveInventory inventory = inventoryBySpace.get(space);
    if (inventory == null) {
      inventory = new PredictiveInventory();
      inventoryBySpace.put(space, inventory);
    }
    return inventory;
  }
}
