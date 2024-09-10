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

import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.ServiceShield;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/** this is much like CoreService EXCEPT it negotiates with the source of truth */
public class ReadOnlyService {
  private final CoreMetrics metrics;
  private final ServiceShield shield;
  private final LivingDocumentFactoryFactory livingDocumentFactoryFactory;
  private final ReadOnlyReplicaThreadBase[] bases;

  // private final ReplicationInitiator;

  public ReadOnlyService(CoreMetrics metrics, ServiceShield shield, LivingDocumentFactoryFactory livingDocumentFactoryFactory, int nThreads) {
    this.metrics = metrics;
    this.shield = shield;
    this.livingDocumentFactoryFactory = livingDocumentFactoryFactory;
    this.bases = new ReadOnlyReplicaThreadBase[nThreads];
    for (int k = 0; k < bases.length; k++) {
      this.bases[k] = new ReadOnlyReplicaThreadBase(k, shield, metrics, SimpleExecutor.create("ro-core-" + k));
    }
  }

  public void shutdown() throws InterruptedException {
    CountDownLatch[] latches = new CountDownLatch[bases.length];
    for (int kThread = 0; kThread < bases.length; kThread++) {
      latches[kThread] = bases[kThread].executor.shutdown();
    }
    for (int kThread = 0; kThread < bases.length; kThread++) {
      latches[kThread].await(1000, TimeUnit.MILLISECONDS);
    }
  }
}
