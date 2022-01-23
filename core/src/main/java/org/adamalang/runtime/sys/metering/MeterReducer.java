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

import org.adamalang.common.TimeSource;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * math for reducing a stream of billing updates for an hour into a summary using P95 for memory and
 * count and SUM for cpu and messages
 */
public class MeterReducer {
  private final TimeSource time;
  private final TreeMap<String, PerSpaceReducer> spaces;

  public MeterReducer(final TimeSource time) {
    this.time = time;
    this.spaces = new TreeMap<>();
  }

  public static long estimateP95(ArrayList<Long> values) {
    values.sort(Long::compareTo);
    int index94 = Math.min((values.size() * 94) / 100, values.size() - 1);
    int index96 = Math.min((values.size() * 96) / 100, values.size() - 1);
    long avg = 0;
    int n = 0;
    for (int k = index94; k <= index96; k++) {
      avg += values.get(k);
      n++;
    }
    return avg / n;
  }

  public void next(MeterReading meterReading) {
    PerSpaceReducer space = spaces.get(meterReading.space);
    if (space == null) {
      space = new PerSpaceReducer();
      spaces.put(meterReading.space, space);
    }
    space.sumCPU += meterReading.cpu;
    space.sumMessages += meterReading.messages;
    space.memorySamples.add(meterReading.memory);
    space.countSamples.add(meterReading.count);
    space.connectionsSamples.add(meterReading.connections);
  }

  public String toJson() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("time");
    writer.writeLong(time.nowMilliseconds());
    writer.writeObjectFieldIntro("spaces");
    writer.beginObject();
    for (Map.Entry<String, PerSpaceReducer> space : spaces.entrySet()) {
      String reduced = space.getValue().reduce();
      if (reduced != null) {
        writer.writeObjectFieldIntro(space.getKey());
        writer.injectJson(reduced);
      }
    }
    writer.endObject();
    writer.endObject();
    return writer.toString();
  }

  private class PerSpaceReducer {
    private final ArrayList<Long> memorySamples;
    private final ArrayList<Long> countSamples;
    private final ArrayList<Long> connectionsSamples;
    private long sumCPU;
    private long sumMessages;

    private PerSpaceReducer() {
      this.sumCPU = 0;
      this.sumMessages = 0;
      this.memorySamples = new ArrayList<>();
      this.countSamples = new ArrayList<>();
      this.connectionsSamples = new ArrayList<>();
    }

    private String reduce() {
      boolean notZero = false;
      countSamples.sort(Long::compareTo);
      JsonStreamWriter writer = new JsonStreamWriter();
      writer.beginObject();
      writer.writeObjectFieldIntro("cpu");
      writer.writeLong(sumCPU);
      if (sumCPU != 0) {
        notZero = true;
      }
      writer.writeObjectFieldIntro("messages");
      writer.writeLong(sumMessages);
      if (sumMessages != 0) {
        notZero = true;
      }
      writer.writeObjectFieldIntro("count_p95");
      long count95 = estimateP95(countSamples);
      writer.writeLong(count95);
      if (count95 != 0) {
        notZero = true;
      }
      writer.writeObjectFieldIntro("memory_p95");
      long memory95 = estimateP95(memorySamples);
      writer.writeLong(memory95);
      if (memory95 != 0) {
        notZero = true;
      }
      writer.writeObjectFieldIntro("connections_p95");
      long connections95 = estimateP95(connectionsSamples);
      writer.writeLong(connections95);
      if (connections95 != 0) {
        notZero = true;
      }
      writer.endObject();
      if (notZero) {
        return writer.toString();
      }
      return null;
    }
  }
}
