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
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockReplicationInitiator;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ReadOnlyReplicaThreadBaseTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final NtPrincipal WHO = new NtPrincipal("me", "a");

  private static final String SIMPLE_CODE =
      "@static { create { return true; } } public int x = 100; message Bump{int x;} channel bump(Bump b) { x+= b.x; } view int echo; bubble foo = @viewer.echo;";

  public static ReadOnlyReplicaThreadBase baseOf(ReplicationInitiator initiator, LivingDocumentFactoryFactory factory) {
    SimpleExecutor executor = SimpleExecutor.create("test");
    MockTime time = new MockTime(1000);
    return new ReadOnlyReplicaThreadBase(0, new ServiceShield(), METRICS, factory, initiator, time, executor);
  }

  @Test
  public void primary_readonly_flow_with_mock_replication() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    base.setInventoryMillisecondsSchedule(10, 5);
    base.setMillisecondsInactivityBeforeCleanup(500);
    Assert.assertEquals(500, base.getMillisecondsInactivityBeforeCleanup());
    MockReadOnlyStream stream = new MockReadOnlyStream();
    Runnable latchAt2 = stream.latchAt(2);
    Runnable latchAt3 = stream.latchAt(3);
    base.observe(ContextSupport.WRAP(WHO), KEY, null, stream);
    stream.await_began();
    latchAt2.run();
    Assert.assertEquals("{\"data\":{\"x\":123,\"foo\":0},\"seq\":0}", stream.get(0));
    Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":0}", stream.get(1));
    stream.get().update("{\"echo\":17}");
    latchAt3.run();
    Assert.assertEquals("{\"data\":{\"foo\":17},\"seq\":0}", stream.get(2));
  }
}
