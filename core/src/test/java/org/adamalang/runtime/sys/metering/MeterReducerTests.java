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
    reducer.next(new MeterReading(1, 120, "space", "hash", new PredictiveInventory.MeteringSample(100, 1000, 10, 200, 17, 123, 456, 789, 13, 420)));
    reducer.next(new MeterReading(1, 120, "mush", "hash", new PredictiveInventory.MeteringSample(100, 1000, 10, 200, 18, 456, 789, 1000, 13, 420)));
    reducer.next(new MeterReading(1, 120, "yo", "hash", new PredictiveInventory.MeteringSample(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)));
    Assert.assertEquals(
        "{\"time\":\"42\",\"spaces\":{\"mush\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\",\"connections_p95\":\"18\",\"bandwidth\":\"456\",\"first_party_service_calls\":\"789\",\"third_party_service_calls\":\"1000\",\"cpu_ms\":\"13\",\"backup_bytes_hours\":\"420\"},\"space\":{\"cpu\":\"1000\",\"messages\":\"200\",\"count_p95\":\"10\",\"memory_p95\":\"100\",\"connections_p95\":\"17\",\"bandwidth\":\"123\",\"first_party_service_calls\":\"456\",\"third_party_service_calls\":\"789\",\"cpu_ms\":\"13\",\"backup_bytes_hours\":\"420\"}}}",
        reducer.toJson());
  }
}
