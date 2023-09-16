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
package org.adamalang.runtime.sys.metering;

import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.sys.PredictiveInventory;

/** a billing for a space */
public class MeterReading {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(MeterReading.class);
  public final long time;
  public final long timeframe;
  public final String space;
  public final String hash;

  public final long memory; // standing --> p95
  public final long cpu; // total --> sum
  public final long count; // standing --> p95
  public final long messages; // total --> sum
  public final long connections; // standing --> p95
  public final long bandwidth; // total -> sum
  public final long first_party_service_calls; // total -> sum
  public final long third_party_service_calls; // total -> sum

  public MeterReading(long time, long timeframe, String space, String hash, PredictiveInventory.MeteringSample meteringSample) {
    this.time = time;
    this.timeframe = timeframe;
    this.space = space;
    this.hash = hash;
    this.memory = meteringSample.memory;
    this.cpu = meteringSample.cpu;
    this.count = meteringSample.count;
    this.messages = meteringSample.messages;
    this.connections = meteringSample.connections;
    this.bandwidth = meteringSample.bandwidth;
    this.first_party_service_calls = meteringSample.first_party_service_calls;
    this.third_party_service_calls = meteringSample.third_party_service_calls;
  }

  public static MeterReading unpack(JsonStreamReader reader) {
    try {
      if (!reader.end() && reader.startArray()) {
        String version = reader.readString();
        if ("v0".equals(version)) {
          long time = reader.readLong();
          long timeframe = reader.readLong();
          String space = reader.readString();
          String hash = reader.readString();
          long memory = reader.readLong();
          long cpu = reader.readLong();
          long count = reader.readLong();
          long messages = reader.readLong();
          long connections = reader.readLong();
          long bandwidth = reader.readLong();
          long first_party_service_calls = reader.readLong();
          long third_party_service_calls = reader.readLong();

          if (!reader.notEndOfArray()) {
            return new MeterReading(time, timeframe, space, hash, new PredictiveInventory.MeteringSample(memory, cpu, count, messages, connections, bandwidth, first_party_service_calls, third_party_service_calls));
          }
        }
        while (reader.notEndOfArray()) {
          reader.skipValue();
        }
      }
    } catch (Exception ex) {
      LOGGER.convertedToErrorCode(ex, -1);
    }
    return null;
  }

  public String packup() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginArray();
    writer.writeString("v0");
    writer.writeLong(time);
    writer.writeLong(timeframe);
    writer.writeString(space);
    writer.writeString(hash);
    writer.writeLong(memory);
    writer.writeLong(cpu);
    writer.writeLong(count);
    writer.writeLong(messages);
    writer.writeLong(connections);
    writer.writeLong(bandwidth);
    writer.writeLong(first_party_service_calls);
    writer.writeLong(third_party_service_calls);
    writer.endArray();
    return writer.toString();
  }
}
