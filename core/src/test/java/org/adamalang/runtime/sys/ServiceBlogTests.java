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
package org.adamalang.runtime.sys;

import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.mocks.MockWakeService;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.*;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

public class ServiceBlogTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String PUBSUB_CODE = "@static {\n" + "  // anyone can create\n" + "  create { return true; }\n" + "}\n" + "\n" + "@connected {\n" + "   // let everyone connect; sure, what can go wrong\n" + "  return true;\n" + "}\n" + "\n" + "// we build a table of publishes with who published it and when they did so\n" + "record Publish {\n" + "  public principal who;\n" + "  public long when;\n" + "  public string payload;\n" + "}\n" + "\n" + "table<Publish> _publishes;\n" + "\n" + "// since tables are private, we expose all publishes to all connected people\n" + "public formula publishes = iterate _publishes order by when asc;\n" + "\n" + "// we wrap a payload inside a message\n" + "message PublishMessage {\n" + "  string payload;\n" + "}\n" + "\n" + "// and then open a channel to accept the publish from any connected client\n" + "channel publish(PublishMessage message) {\n" + "  _publishes <- {who: @who, when: Time.now(), payload: message.payload };\n" + "  \n" + "  // At this point, we encounter a key problem with maintaining a\n" + "  // log of publishes. Namely, the log is potentially infinite, so\n" + "  // we have to leverage some product intelligence to reduce it to\n" + "  // a reasonably finite set which is important for the product.\n" + "\n" + "  // First, we age out publishes too old (sad face)\n" + "  (iterate _publishes\n" + "     where when < Time.now() - 60000L).delete();\n" + "  \n" + "  // Second, we hard cap the publishes biasing younger ones\n" + "  (iterate _publishes\n" + "     order by when desc\n" + "     limit _publishes.size() offset 100).delete();\n" + "     \n" + "  // Hindsight: I should decouple the offset from\n" + "  // the limit because this is currently silly (TODO)\n" + "}";
  private static final String MAXSEQ_CODE = "@static {\n" + "  create { return true; }\n" + "}\n" + "\n" + "@connected {\n" + "  return true;\n" + "}\n" + "\n" + "public int max_db_seq = 0;\n" + "\n" + "message NotifyWrite {\n" + "  int db_seq;\n" + "}\n" + "\n" + "channel notify(NotifyWrite message) {\n" + "  if (message.db_seq > max_db_seq) {\n" + "    max_db_seq = message.db_seq;\n" + "  }\n" + "}";

  @Test
  public void test20220202_pubsub() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(PUBSUB_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", "1", created);
      created.await_success();

      MockStreamback streamback = new MockStreamback();
      Runnable got = streamback.latchAt(4);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();

      {
        LatchCallback cb = new LatchCallback();
        streamback.get().send("publish", null, "{\"payload\":\"x\"}", cb);
        cb.await_success(5);
      }
      time.time = 100000;
      {
        LatchCallback cb = new LatchCallback();
        streamback.get().send("publish", null, "{\"payload\":\"y\"}", cb);
        cb.await_success(6);
      }

      got.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"seq\":4}", streamback.get(1));
      Assert.assertEquals("{\"data\":{\"publishes\":{\"1\":{\"who\":{\"@t\":1,\"agent\":\"?\",\"authority\":\"?\"},\"when\":\"0\",\"payload\":\"x\"},\"@o\":[1]}},\"seq\":5}", streamback.get(2));
      Assert.assertEquals("{\"data\":{\"publishes\":{\"2\":{\"who\":{\"@t\":1,\"agent\":\"?\",\"authority\":\"?\"},\"when\":\"100000\",\"payload\":\"y\"},\"@o\":[2],\"1\":null}},\"seq\":6}", streamback.get(3));
    } finally {
      service.shutdown();
    }
  }

  @Test
  public void test20220202_maxseq() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(MAXSEQ_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", "1", created);
      created.await_success();

      MockStreamback streamback = new MockStreamback();
      Runnable got = streamback.latchAt(4);
      Runnable gotInv = streamback.latchAt(5);
      service.connect(ContextSupport.WRAP(NtPrincipal.NO_ONE), KEY, "{}", ConnectionMode.Full, streamback);
      streamback.await_began();

      {
        LatchCallback cb = new LatchCallback();
        streamback.get().send("notify", null, "{\"db_seq\":4}", cb);
        cb.await_success(5);
      }
      time.time = 100000;
      {
        LatchCallback cb = new LatchCallback();
        streamback.get().send("notify", null, "{\"db_seq\":6}", cb);
        cb.await_success(6);
      }

      got.run();
      Assert.assertEquals("STATUS:Connected", streamback.get(0));
      Assert.assertEquals("{\"data\":{\"max_db_seq\":0},\"seq\":4}", streamback.get(1));
      Assert.assertEquals("{\"data\":{\"max_db_seq\":4},\"seq\":5}", streamback.get(2));
      Assert.assertEquals("{\"data\":{\"max_db_seq\":6},\"seq\":6}", streamback.get(3));

      service.invalidateAll();
      gotInv.run();
      Assert.assertEquals("{\"seq\":7}", streamback.get(4));
    } finally {
      service.shutdown();
    }
  }
}
