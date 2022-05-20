/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.sm;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.InstanceClientFinder;
import org.adamalang.net.client.TestClientConfig;
import org.adamalang.net.client.routing.MockSpaceTrackingEvents;
import org.adamalang.net.client.routing.reactive.ReativeRoutingEngine;
import org.adamalang.net.mocks.LatchedSeqCallback;
import org.adamalang.net.mocks.MockSimpleEvents;
import org.junit.Test;

public class ConnectionTrafficShift {

  @Test
  public void validateTrafficShiftOnNewHost() throws Exception {
    ClientMetrics metrics = new ClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SimpleExecutor executor = SimpleExecutor.create("executor");
    ExceptionLogger logger = (t, c) -> {};
    try {
      ClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      // we use the direct engine to control the connection... directly
      ReativeRoutingEngine engine = new ReativeRoutingEngine(metrics, executor, new MockSpaceTrackingEvents(), 5, 5);
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 2, engine, logger);
      try {
        // finder.sync(Helper.setOf("127.0.0.1:20005", "127.0.0.1:20006"));
        finder.sync(Helper.setOf("127.0.0.1:20005"));
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable eventsProducedData = events.latchAt(2);
        Runnable eventsGotUpdate = events.latchAt(3);
        Runnable eventsGotRollback = events.latchAt(4);
        Runnable eventFailed = events.latchAt(5);
        ConnectionBase base = new ConnectionBase(clientConfig, metrics, engine, finder, executor);
        Connection connection = new Connection(base, "127.0.0.1",  "origin", "who", "dev", "space", "key", "{}", null, events);
        connection.open();
        eventsProducedData.run();
        events.assertWrite(0, "CONNECTED");
        events.assertWrite(1, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        LatchedSeqCallback cb1 = new LatchedSeqCallback();
        connection.send("foo", null, "{\"z\":100}", cb1);
        eventsGotUpdate.run();
        events.assertWrite(2, "DELTA:{\"data\":{\"x\":223},\"seq\":5}");
        finder.sync(Helper.setOf("127.0.0.1:20006"));
        eventsGotRollback.run();
        events.assertWrite(3, "DELTA:{\"data\":{\"x\":123},\"seq\":4}");
        finder.sync(Helper.setOf());
        eventFailed.run();
        events.assertWrite(4, "ERROR:992319");
      } finally {
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
      executor.shutdown();
    }
  }
}
