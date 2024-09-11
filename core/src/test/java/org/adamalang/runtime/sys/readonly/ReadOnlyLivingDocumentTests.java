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
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.replication.SequencedTestExecutor;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.LivingDocument;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockReplicationInitiator;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReadOnlyLivingDocumentTests {
  @Test
  public void esoteric_cancel_kill_race() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(ReadOnlyReplicaThreadBaseTests.SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    SequencedTestExecutor executor = new SequencedTestExecutor();
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = ReadOnlyReplicaThreadBaseTests.baseOf(executor, seed, factoryFactory);
    LivingDocument real = factory.create(null);
    ReadOnlyLivingDocument document = new ReadOnlyLivingDocument(base, ReadOnlyReplicaThreadBaseTests.KEY, real);
    document.kill();
    AtomicBoolean setInstant = new AtomicBoolean(false);
    document.setCancel(() -> {
      setInstant.set(true);
    });
    Assert.assertTrue(setInstant.get());
  }
}
