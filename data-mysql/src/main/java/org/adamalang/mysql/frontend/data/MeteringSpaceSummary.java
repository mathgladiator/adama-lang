/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.frontend.data;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.HashMap;

/** the summary of the space used within a window */
public class MeteringSpaceSummary {
  private final HashMap<String, MeteringSummaryPartialPerTarget> targets;
  private long cpuTicks;
  private long messages;
  private long storageBytes;
  private long unbilledStorageByteHours;

  public MeteringSpaceSummary() {
    this.targets = new HashMap<>();
    this.cpuTicks = 0;
    this.storageBytes = 0;
    this.unbilledStorageByteHours = 0;
  }

  public void setStorageBytes(long storageBytes) {
    this.storageBytes = storageBytes;
  }

  public void setUnbilledStorageByteHours(long unbilledStorageByteHours) {
    this.unbilledStorageByteHours = unbilledStorageByteHours;
  }

  public void include(String target, JsonNode node) {
    MeteringSummaryPartialPerTarget byTarget = targets.get(target);
    if (byTarget == null) {
      byTarget = new MeteringSummaryPartialPerTarget();
      targets.put(target, byTarget);
    }
    cpuTicks += node.get("cpu").asLong();
    messages += node.get("messages").asLong();
    byTarget.include(node.get("count_p95").asLong(), node.get("memory_p95").asLong(), node.get("connections_p95").asLong());
  }

  public MeteredWindowSummary summarize(ResourcesPerPenny rates) {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    if (cpuTicks > 0) {
      writer.writeObjectFieldIntro("cpu");
      writer.writeLong(cpuTicks);
    }
    if (messages > 0) {
      writer.writeObjectFieldIntro("messages");
      writer.writeLong(messages);
    }
    long count = 0;
    long memory = 0;
    long connections = 0;
    for (MeteringSummaryPartialPerTarget target : targets.values()) {
      count += target.count;
      memory += target.memory;
      connections += target.connections;
    }
    if (count > 0) {
      writer.writeObjectFieldIntro("count");
      writer.writeLong(count);
    }
    if (memory > 0) {
      writer.writeObjectFieldIntro("memory");
      writer.writeLong(memory);
    }
    if (connections > 0) {
      writer.writeObjectFieldIntro("connections");
      writer.writeLong(connections);
    }
    if (storageBytes > 0) {
      writer.writeObjectFieldIntro("storageBytes");
      writer.writeLong(storageBytes);
    }
    writer.endObject();
    long totalStorageByteHours = storageBytes + unbilledStorageByteHours;
    int pennies = (int) Math.ceil(max(messages / rates.messages, count / rates.count, memory / rates.memory, connections / rates.connections, cpuTicks / rates.cpu)) + (int) (totalStorageByteHours / rates.storage);
    return new MeteredWindowSummary(writer.toString(), pennies, storageBytes, (totalStorageByteHours % rates.storage) - unbilledStorageByteHours);
  }

  private static double max(double... values) {
    double value = values[0];
    for (int k = 1; k < values.length; k++) {
      if (values[k] > value) {
        value = values[k];
      }
    }
    return value;
  }

}
