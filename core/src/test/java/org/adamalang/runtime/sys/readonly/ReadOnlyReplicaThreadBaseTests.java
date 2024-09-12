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

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.DeploymentMonitor;
import org.adamalang.runtime.contracts.LivingDocumentFactoryFactory;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.replication.SequencedTestExecutor;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockReplicationInitiator;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReadOnlyReplicaThreadBaseTests {
  public static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  public static final Key KEY = new Key("space", "key");
  public static final NtPrincipal WHO = new NtPrincipal("me", "a");
  public static final String SIMPLE_CODE = "@static { create { return true; } } public int x = 100; message Bump{int x;} channel bump(Bump b) { x+= b.x; } view int echo; bubble foo = @viewer.echo;";
  public static final String SIMPLE_CODE_DEPLOYED = "@static { create { return true; } } public int x = 100; public int y = 72; message Bump{int x;} channel bump(Bump b) { x+= b.x; } view int echo; bubble foo = @viewer.echo;";

  public static ReadOnlyReplicaThreadBase baseOf(ReplicationInitiator initiator, LivingDocumentFactoryFactory factory) {
    SimpleExecutor executor = SimpleExecutor.create("test");
    MockTime time = new MockTime(1000);
    return new ReadOnlyReplicaThreadBase(0, new ServiceShield(), METRICS, factory, initiator, time, executor);
  }

  public static ReadOnlyReplicaThreadBase baseOf(SimpleExecutor executor, ReplicationInitiator initiator, LivingDocumentFactoryFactory factory) {
    MockTime time = new MockTime(1000);
    return new ReadOnlyReplicaThreadBase(0, new ServiceShield(), METRICS, factory, initiator, time, executor);
  }

  @Test
  public void primary_readonly_flow_with_mock_replication() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      base.setInventoryMillisecondsSchedule(10, 5);
      base.setMillisecondsInactivityBeforeCleanup(500);
      Assert.assertEquals(500, base.getMillisecondsInactivityBeforeCleanup());
      MockReadOnlyStream stream = new MockReadOnlyStream();
      Runnable latchAt2 = stream.latchAt(3);
      Runnable latchAt3 = stream.latchAt(4);
      base.observe(ContextSupport.WRAP(WHO), KEY, null, stream);
      stream.await_began();
      latchAt2.run();
      Assert.assertEquals("{\"data\":{\"x\":123,\"foo\":0},\"seq\":0}", stream.get(1));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":0}", stream.get(2));
      stream.get().update("{\"echo\":17}");
      latchAt3.run();
      Assert.assertEquals("{\"data\":{\"foo\":17},\"seq\":0}", stream.get(3));
    } finally {
      base.close();
    }
  }

  @Test
  public void cantConnectShield_Existing() throws Exception{
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      base.shield.canConnectExisting.set(false);
      MockReadOnlyStream stream = new MockReadOnlyStream();
      base.observe(ContextSupport.WRAP(WHO), KEY, null, stream);
      stream.await_failure(183499);
    } finally {
      base.close();
    }
  }

  @Test
  public void cantConnectShield_New() throws Exception{
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      base.shield.canConnectNew.set(false);
      MockReadOnlyStream stream = new MockReadOnlyStream();
      base.observe(ContextSupport.WRAP(WHO), KEY, null, stream);
      stream.await_failure(146632);
    } finally {
      base.close();
    }
  }

  @Test
  public void race_multiple_connections() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    SequencedTestExecutor executor = new SequencedTestExecutor();
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(executor, seed, factoryFactory);
    MockReadOnlyStream stream1 = new MockReadOnlyStream();
    MockReadOnlyStream stream2 = new MockReadOnlyStream();
    base.observe(ContextSupport.WRAP(WHO), KEY, null, stream1);
    executor.next();
    base.observe(ContextSupport.WRAP(WHO), KEY, null, stream2);
    executor.swap();
    NamedRunnable e = executor.extract();
    executor.drain();
    e.run();
    executor.drain();
    stream1.await_began();
    stream2.await_began();
  }

  @Test
  public void inventory_empty() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    SequencedTestExecutor executor = new SequencedTestExecutor();
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(executor, seed, factoryFactory);
    base.kickOffInventory();
    AtomicBoolean ranMetering = new AtomicBoolean(false);
    base.sampleMetering((map) -> {
      ranMetering.set(true);
      Assert.assertEquals(0, map.size());
    });
    executor.next();
    executor.next();
    Assert.assertTrue(ranMetering.get());
  }

  @Test
  public void inventory_solo_viewstate() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", null);
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      base.setMillisecondsInactivityBeforeCleanup(1);
      MockReadOnlyStream stream = new MockReadOnlyStream();
      Runnable latch = stream.latchAt(2);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k1"), "{\"echo\":13}", stream);
      stream.await_began();
      latch.run();
      Assert.assertEquals("{\"data\":{\"x\":123,\"foo\":13},\"seq\":0}", stream.get(1));
    } finally {
      base.close();
    }
  }

  @Test
  public void inventory_solo_kills() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    SequencedTestExecutor executor = new SequencedTestExecutor();
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(executor, seed, factoryFactory);
    try {
      base.setMillisecondsInactivityBeforeCleanup(1);
      MockReadOnlyStream stream1 = new MockReadOnlyStream();
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k1"), null, stream1);
      executor.drain();
      base.kickOffInventory();
      AtomicBoolean ranMetering = new AtomicBoolean(false);
      base.sampleMetering((map) -> {
        PredictiveInventory.MeteringSample sample = map.get("s");
        Assert.assertTrue(sample.memory > 0);
        ranMetering.set(true);
      });
      executor.wave();
      Assert.assertTrue(ranMetering.get());
      stream1.get().close();
      executor.wave();
      ((MockTime) base.time).set(100000);
      executor.wave();
      ranMetering.set(false);
      base.sampleMetering((map) -> {
        ranMetering.set(map.size() == 0);
      });
      executor.wave();
      Assert.assertTrue(ranMetering.get());
    } finally {
      base.close();
    }
  }

  @Test
  public void inventory_many() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    SequencedTestExecutor executor = new SequencedTestExecutor();
    ReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(executor, seed, factoryFactory);
    try {
      base.setMillisecondsInactivityBeforeCleanup(1);
      MockReadOnlyStream stream1 = new MockReadOnlyStream();
      MockReadOnlyStream stream2 = new MockReadOnlyStream();
      MockReadOnlyStream stream3 = new MockReadOnlyStream();
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k1"), null, stream1);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k2"), null, stream2);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k3"), null, stream3);
      base.shed((key) -> false); // no-op
      executor.drain();
      base.kickOffInventory();
      AtomicBoolean ranMetering = new AtomicBoolean(false);
      base.sampleMetering((map) -> {
        PredictiveInventory.MeteringSample sample = map.get("s");
        Assert.assertTrue(sample.memory > 0);
        Assert.assertEquals(3, sample.count);
        ranMetering.set(true);
      });
      executor.wave();
      Assert.assertTrue(ranMetering.get());
    } finally {
      base.close();
    }
  }

  @Test
  public void failure_kills_observers() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    MockReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      MockReadOnlyStream stream = new MockReadOnlyStream();
      Runnable latch2 = stream.latchAt(3);
      Runnable latch3 = stream.latchAt(4);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream);
      stream.await_began();
      latch2.run();
      Assert.assertEquals("{\"data\":{\"x\":123,\"foo\":7},\"seq\":0}", stream.get(1));
      Assert.assertEquals("{\"data\":{\"x\":42},\"seq\":0}", stream.get(2));
      seed.getLastObserver().failure(new ErrorCodeException(1000));
      latch3.run();
      Assert.assertEquals("CLOSED", stream.get(3));
    } finally {
      base.close();
    }
  }

  @Test
  public void failed_create_factory() throws Exception {
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(null);
    MockReplicationInitiator seed = new MockReplicationInitiator("{\"x\":123}", "{\"x\":42}");
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      MockReadOnlyStream stream = new MockReadOnlyStream();
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream);
      stream.await_failure(999);
    } finally {
      base.close();
    }
  }

  @Test
  public void failed_init() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    MockReplicationInitiator seed = new MockReplicationInitiator(null, null);
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      MockReadOnlyStream stream = new MockReadOnlyStream();
      Runnable latched = stream.latchAt(2);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream);
      latched.run();
      Assert.assertEquals("CLOSED", stream.get(1));
    } finally {
      base.close();
    }
  }

  @Test
  public void concurrent_joins_post_failure() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    MockReplicationInitiator seed = new MockReplicationInitiator(null, null);
    seed.setDelayed();
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      MockReadOnlyStream stream1 = new MockReadOnlyStream();
      Runnable latched1 = stream1.latchAt(2);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream1);
      MockReadOnlyStream stream2 = new MockReadOnlyStream();
      Runnable latched2 = stream2.latchAt(2);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream2);
      seed.executeDelay();
      latched1.run();
      latched2.run();
      Assert.assertEquals("CLOSED", stream1.get(1));
      Assert.assertEquals("CLOSED", stream2.get(1));
    } finally {
      base.close();
    }
  }

  @Test
  public void shed() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    MockReplicationInitiator seed = new MockReplicationInitiator("{\"x\":42}", null);
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      MockReadOnlyStream stream = new MockReadOnlyStream();
      Runnable latched1 = stream.latchAt(2);
      Runnable latched2 = stream.latchAt(3);
      base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream);
      latched1.run();
      Assert.assertEquals("{\"data\":{\"x\":42,\"foo\":7},\"seq\":0}", stream.get(1));
      base.shed((key) -> true);
      latched2.run();
      Assert.assertEquals("CLOSED", stream.get(2));
    } finally {
      base.close();
    }
  }

  @Test
  public void deployment_kills() throws Exception {
    LivingDocumentFactory factory1 = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    LivingDocumentFactory factory2 = LivingDocumentTests.compile(SIMPLE_CODE_DEPLOYED, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory1);
    MockReplicationInitiator seed = new MockReplicationInitiator("{\"x\":42}", null);
    ReadOnlyReplicaThreadBase base = baseOf(seed, factoryFactory);
    try {
      {
        MockReadOnlyStream stream = new MockReadOnlyStream();
        Runnable latched1 = stream.latchAt(2);
        Runnable latched2 = stream.latchAt(3);
        base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream);
        latched1.run();
        Assert.assertEquals("{\"data\":{\"x\":42,\"foo\":7},\"seq\":0}", stream.get(1));
        factoryFactory.set(factory2);
        base.deploy(new DeploymentMonitor() {
          @Override
          public void bumpDocument(boolean changed) {

          }

          @Override
          public void witnessException(ErrorCodeException ex) {

          }

          @Override
          public void finished(int ms) {

          }
        });
        latched2.run();
        Assert.assertEquals("CLOSED", stream.get(2));
      }
      {
        MockReadOnlyStream stream = new MockReadOnlyStream();
        Runnable latched1 = stream.latchAt(2);
        base.observe(ContextSupport.WRAP(WHO), new Key("s", "k"), "{\"echo\":7}", stream);
        latched1.run();
        Assert.assertEquals("{\"data\":{\"x\":42,\"y\":72,\"foo\":7},\"seq\":0}", stream.get(1));
      }
    } finally {
      base.close();
    }
  }
}
