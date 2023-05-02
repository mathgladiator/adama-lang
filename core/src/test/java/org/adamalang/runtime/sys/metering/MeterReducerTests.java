/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

public class MeterReducerTests {
  @Test
  public void flow() {
    MockTime time = new MockTime();
    time.set(42);
    MeterReducer reducer = new MeterReducer(time);
    Assert.assertEquals("{\"time\":\"42\",\"spaces\":{}}", reducer.toJson());
    reducer.next(new MeterReading(1, 120, "space", "hash", new PredictiveInventory.MeteringSample(100, 1000, 10, 200, 17, 123, 456, 789)));
    reducer.next(new MeterReading(1, 120, "mush", "hash", new PredictiveInventory.MeteringSample(100, 1000, 10, 200, 18, 456, 789, 1000)));
    reducer.next(new MeterReading(1, 120, "yo", "hash", new PredictiveInventory.MeteringSample(0, 0, 0, 0, 0, 0, 0, 0)));
    Assert.assertEquals(
        "{\"time\":\"42\",\"spaces\":{\"mush\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\",\"connections_p95\":\"18\",\"bandwidth\":\"456\",\"first_party_service_calls\":\"789\",\"third_party_service_calls\":\"1000\"},\"space\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\",\"connections_p95\":\"17\",\"bandwidth\":\"123\",\"first_party_service_calls\":\"456\",\"third_party_service_calls\":\"789\"}}}",
        reducer.toJson());
  }
}
