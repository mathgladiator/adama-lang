/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.metering;

import org.adamalang.common.Json;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class MeterReducerReaderTests {

  @Test
  public void convert() {
    MockTime time = new MockTime();
    time.set(42);
    MeterReducer reducer = new MeterReducer(time);
    Assert.assertEquals("{\"time\":\"42\",\"spaces\":{}}", reducer.toJson());
    reducer.next(new MeterReading(1, 120, "space", "hash", new PredictiveInventory.MeteringSample(100, 1000, 10, 200, 17, 123, 456, 789)));
    reducer.next(new MeterReading(1, 120, "mush", "hash", new PredictiveInventory.MeteringSample(100, 1000, 10, 200, 18, 456, 789, 1000)));
    reducer.next(new MeterReading(1, 120, "yo", "hash", new PredictiveInventory.MeteringSample(0, 0, 0, 0, 0, 0, 0, 0)));

    Map<String, String> messages = MeterReducerReader.convertMapToBillingMessages(reducer.toJson(), "region-a", "the-machine");
    Assert.assertEquals("{\"timestamp\":\"42\",\"space\":\"space\",\"region\":\"region-a\",\"machine\":\"the-machine\",\"record\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\",\"connections_p95\":\"17\",\"bandwidth\":\"123\",\"first_party_service_calls\":\"456\",\"third_party_service_calls\":\"789\"}}", messages.get("space"));
    Assert.assertEquals("{\"timestamp\":\"42\",\"space\":\"mush\",\"region\":\"region-a\",\"machine\":\"the-machine\",\"record\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\",\"connections_p95\":\"18\",\"bandwidth\":\"456\",\"first_party_service_calls\":\"789\",\"third_party_service_calls\":\"1000\"}}", messages.get("mush"));
    Assert.assertFalse(messages.containsKey("yo"));
    Json.parseJsonObject(messages.get("space"));
    Json.parseJsonObject(messages.get("mush"));
  }
}
