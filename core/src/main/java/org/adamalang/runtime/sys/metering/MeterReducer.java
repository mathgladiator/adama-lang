/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    space.sumBandwidth += meterReading.bandwidth;
    space.sumFirstPartyServiceCalls += meterReading.first_party_service_calls;
    space.sumThirdPartyServiceCalls += meterReading.third_party_service_calls;
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
    private long sumBandwidth;
    private long sumFirstPartyServiceCalls;
    private long sumThirdPartyServiceCalls;

    private PerSpaceReducer() {
      this.sumCPU = 0;
      this.sumMessages = 0;
      this.memorySamples = new ArrayList<>();
      this.countSamples = new ArrayList<>();
      this.connectionsSamples = new ArrayList<>();
      this.sumBandwidth = 0;
      this.sumFirstPartyServiceCalls = 0;
      this.sumThirdPartyServiceCalls = 0;
    }

    private String reduce() {
      boolean notZero = false;
      countSamples.sort(Long::compareTo);
      JsonStreamWriter writer = new JsonStreamWriter();
      writer.beginObject();
      if (sumCPU != 0) {
        writer.writeObjectFieldIntro("cpu");
        writer.writeLong(sumCPU);
        notZero = true;
      }
      if (sumMessages != 0) {
        writer.writeObjectFieldIntro("messages");
        writer.writeLong(sumMessages);
        notZero = true;
      }
      long count95 = estimateP95(countSamples);
      if (count95 != 0) {
        writer.writeObjectFieldIntro("count_p95");
        writer.writeLong(count95);
        notZero = true;
      }
      writer.writeObjectFieldIntro("memory_p95");
      long memory95 = estimateP95(memorySamples);
      writer.writeLong(memory95);
      if (memory95 != 0) {
        notZero = true;
      }
      long connections95 = estimateP95(connectionsSamples);
      if (connections95 != 0) {
        writer.writeObjectFieldIntro("connections_p95");
        writer.writeLong(connections95);
        notZero = true;
      }
      if (sumBandwidth != 0) {
        writer.writeObjectFieldIntro("bandwidth");
        writer.writeLong(sumBandwidth);
        notZero = true;
      }

      if (sumFirstPartyServiceCalls != 0) {
        writer.writeObjectFieldIntro("first_party_service_calls");
        writer.writeLong(sumFirstPartyServiceCalls);
        notZero = true;
      }

      if (sumThirdPartyServiceCalls != 0) {
        writer.writeObjectFieldIntro("third_party_service_calls");
        writer.writeLong(sumThirdPartyServiceCalls);
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
