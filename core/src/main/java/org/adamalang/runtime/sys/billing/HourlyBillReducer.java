/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.billing;

import org.adamalang.common.TimeSource;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/** math for reducing a stream of billing updates for an hour into a summary using P95 for memory and count and SUM for cpu and messages */
public class HourlyBillReducer {
  private final TimeSource time;

  private class PerSpaceReducer {
    private long sumCPU;
    private long sumMessages;
    private ArrayList<Long> memorySamples;
    private ArrayList<Long> countSamples;

    private PerSpaceReducer() {
      this.sumCPU = 0;
      this.sumMessages = 0;
      this.memorySamples = new ArrayList<>();
      this.countSamples = new ArrayList<>();
    }

    public void writeAsObject(JsonStreamWriter writer) {
      countSamples.sort(Long::compareTo);
      writer.beginObject();
      writer.writeObjectFieldIntro("cpu");
      writer.writeLong(sumCPU);
      writer.writeObjectFieldIntro("messages");
      writer.writeLong(sumMessages);
      writer.writeObjectFieldIntro("count_p95");
      writer.writeLong(estimateP95(countSamples));
      writer.writeObjectFieldIntro("memory_p95");
      writer.writeLong(estimateP95(memorySamples));
      writer.endObject();
    }
  }
  private final TreeMap<String, PerSpaceReducer> spaces;

  public HourlyBillReducer(final TimeSource time) {
    this.time = time;
    this.spaces = new TreeMap<>();
  }

  public void next(Bill bill) {
    PerSpaceReducer space = spaces.get(bill.space);
    if (space == null) {
      space = new PerSpaceReducer();
      spaces.put(bill.space, space);
    }
    space.sumCPU += bill.cpu;
    space.sumMessages += bill.messages;
    space.memorySamples.add(bill.memory);
    space.countSamples.add(bill.count);
  }

  public String toJson() {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("time");
    writer.writeLong(time.nowMilliseconds());
    writer.writeObjectFieldIntro("spaces");
    writer.beginObject();
    for (Map.Entry<String, PerSpaceReducer> space : spaces.entrySet()) {
      writer.writeObjectFieldIntro(space.getKey());
      space.getValue().writeAsObject(writer);
    }
    writer.endObject();
    writer.endObject();
    return writer.toString();
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
}
