/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
    writer.endArray();
    return writer.toString();
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
          if (!reader.notEndOfArray()) {
            return new MeterReading(
                time,
                timeframe,
                space,
                hash,
                new PredictiveInventory.MeteringSample(memory, cpu, count, messages, connections));
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
}
