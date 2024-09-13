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
package org.adamalang.net.client.sm;

import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.net.TestBed;
import org.adamalang.net.client.ClientConfig;
import org.adamalang.net.client.InstanceClientFinder;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.net.client.TestClientConfig;
import org.adamalang.net.client.mocks.MockRoutingTarget;
import org.adamalang.net.client.mocks.SimpleIntCallback;
import org.adamalang.net.mocks.LatchedSeqCallback;
import org.adamalang.net.mocks.LatchedVoidCallback;
import org.adamalang.net.mocks.MockSimpleEvents;
import org.adamalang.runtime.sys.ConnectionMode;
import org.junit.Test;

import java.util.ArrayList;

public class ObservationTests {

  @Test
  public void happy() throws Exception {
    LocalRegionClientMetrics metrics = new LocalRegionClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SimpleExecutor executor = SimpleExecutor.create("executor");
    ExceptionLogger logger = (t, c) -> {};
    try {
      ClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.SIMPLE);
      MockRoutingTarget target = new MockRoutingTarget();
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 2, target, logger);
      try {
        StateMachineBase base = new StateMachineBase(clientConfig, metrics, finder, executor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable gotConnected = events.latchAt(1);
        Runnable gotData = events.latchAt(2);
        Observation observe = new Observation(base, "127.0.0.1:" + servers[0].port, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", 1000, events);
        ArrayList<LatchedVoidCallback> callbacksUpdates = new ArrayList<>();
        for (int k = 0; k < 2; k++) {
          LatchedVoidCallback updateRan = new LatchedVoidCallback();
          observe.update("{\"k\":" + k + "}", updateRan);
          callbacksUpdates.add(updateRan);
        }
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port));
        observe.open();
        gotConnected.run();
        gotData.run();
        for (LatchedVoidCallback callback : callbacksUpdates) {
          callback.assertSuccess();
        }
        observe.close();
      } finally {
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
    }
  }
  
  @Test
  public void sad_bad_code() throws Exception {
    LocalRegionClientMetrics metrics = new LocalRegionClientMetrics(new NoOpMetricsFactory());
    TestBed[] servers = new TestBed[2];
    SimpleExecutor executor = SimpleExecutor.create("executor");
    ExceptionLogger logger = (t, c) -> {};
    try {
      ClientConfig clientConfig = new TestClientConfig();
      ComplexHelper.spinUpCapacity(servers, true, ComplexHelper.BAD_CODE);
      MockRoutingTarget target = new MockRoutingTarget();
      InstanceClientFinder finder = new InstanceClientFinder(servers[0].base, clientConfig, metrics, null, SimpleExecutorFactory.DEFAULT, 2, target, logger);
      try {
        StateMachineBase base = new StateMachineBase(clientConfig, metrics, finder, executor);
        MockSimpleEvents events = new MockSimpleEvents();
        Runnable gotConnected = events.latchAt(1);
        Runnable gotData = events.latchAt(2);
        Observation connection = new Observation(base, "127.0.0.1:" + servers[0].port, "127.0.0.1", "origin", "who", "dev", "space", "key", "{}", 1000, events);
        ArrayList<LatchedVoidCallback> callbacksUpdates = new ArrayList<>();
        for (int k = 0; k < 20; k++) {
          LatchedVoidCallback updateRan = new LatchedVoidCallback();
          connection.update("{\"k\":" + k + "}", updateRan);
          callbacksUpdates.add(updateRan);
        }
        finder.sync(Helper.setOf("127.0.0.1:" + servers[0].port));
        connection.open();
        gotConnected.run();
        gotData.run();
        for (LatchedVoidCallback callback : callbacksUpdates) {
          callback.assertJustComplete();
        }
        connection.close();
      } finally {
        finder.shutdown();
      }
    } finally {
      ComplexHelper.stopCapacity(servers);
    }
  }
}
