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
package org.adamalang.runtime.sys;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.runtime.mocks.MockLivingDocument;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.remote.ServiceRegistry;

import org.junit.Assert;
import org.junit.Test;

public class PerfTrackerTests {
  @Test
  public void flow() throws Exception {
    MockLivingDocument doc = new MockLivingDocument();
    doc.__lateBind("space", "key", Deliverer.FAILURE, new ServiceRegistry());
    PerfTracker tracker = new PerfTracker(doc);
    tracker.measure("xyz").run();
    Runnable x = tracker.measure("xyz");
    Runnable y = tracker.measure("cost");
    doc.__code_cost += 100;
    Thread.sleep(5);
    y.run();
    x.run();
    tracker.measure("xyz").run();
    String result = tracker.dump();
    System.out.println(result);
    Assert.assertTrue(result.contains("\"type\":\"document\""));
    Assert.assertTrue(result.contains("\"avg_cost\":100.0"));
    Assert.assertNull(tracker.dump());
  }

  @Test
  public void lightning() {
    MockLivingDocument doc = new MockLivingDocument();
    doc.__lateBind("space", "key", Deliverer.FAILURE, new ServiceRegistry());
    PerfTracker tracker = new PerfTracker(doc);
    tracker.measureLightning();
    Runnable x = tracker.measure("foo");
    Runnable y = tracker.measure("dip");
    y.run();
    x.run();
    ObjectNode report = Json.parseJsonObject(tracker.getLightningJsonAndReset());
    String normalized = report.toString().replaceAll("[0-9]+", "x");
    Assert.assertEquals("{\"foo\":{\"dip\":{\"__ms\":x},\"__ms\":x}}", normalized);
  }
}
