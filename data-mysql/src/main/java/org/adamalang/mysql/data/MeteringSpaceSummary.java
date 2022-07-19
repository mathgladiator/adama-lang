/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.data;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.HashMap;

/** the summary of the space used within a window */
public class MeteringSpaceSummary {
  private final HashMap<String, MeteringSummaryPartialPerTarget> targets;
  private long cpuTicks;
  private long messages;
  private long storageBytes;
  private UnbilledResources unbilledResources;
  private long bandwidth;
  private long first_party_service_calls;
  private long third_party_service_calls;

  public MeteringSpaceSummary() {
    this.targets = new HashMap<>();
    this.cpuTicks = 0;
    this.storageBytes = 0;
    this.unbilledResources = new UnbilledResources(0, 0, 0, 0);
    this.bandwidth = 0;
    this.first_party_service_calls = 0;
    this.third_party_service_calls = 0;
  }

  public void setStorageBytes(long storageBytes) {
    this.storageBytes = storageBytes;
  }

  public void setUnbilled(UnbilledResources unbilledResources) {
    this.unbilledResources = unbilledResources;
  }

  public void include(String target, JsonNode node) {
    MeteringSummaryPartialPerTarget byTarget = targets.get(target);
    if (byTarget == null) {
      byTarget = new MeteringSummaryPartialPerTarget();
      targets.put(target, byTarget);
    }
    if (node.has("cpu")) {
      cpuTicks += node.get("cpu").asLong();
    }
    if (node.has("messages")) {
      messages += node.get("messages").asLong();
    }
    if (node.has("bandwidth")) {
      bandwidth += node.get("bandwidth").asLong();
    }
    if (node.has("first_party_service_calls")) {
      first_party_service_calls += node.get("first_party_service_calls").asLong();
    }
    if (node.has("third_party_service_calls")) {
      third_party_service_calls += node.get("third_party_service_calls").asLong();
    }
    byTarget.include(
        node.has("count_p95") ? node.get("count_p95").asLong() : 0, //
        node.has("memory_p95") ? node.get("memory_p95").asLong() : 0, //
        node.has("connections_p95") ? node.get("connections_p95").asLong() : 0);
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
    if (bandwidth > 0) {
      writer.writeObjectFieldIntro("bandwidth");
      writer.writeLong(bandwidth);
    }
    if (connections > 0) {
      writer.writeObjectFieldIntro("connections");
      writer.writeLong(connections);
    }
    if (storageBytes > 0) {
      writer.writeObjectFieldIntro("storageBytes");
      writer.writeLong(storageBytes);
    }
    if (bandwidth > 0) {
      writer.writeObjectFieldIntro("bandwidth");
      writer.writeLong(bandwidth);
    }
    if (first_party_service_calls > 0) {
      writer.writeObjectFieldIntro("first_party_service_calls");
      writer.writeLong(first_party_service_calls);
    }
    if (third_party_service_calls > 0) {
      writer.writeObjectFieldIntro("third_party_service_calls");
      writer.writeLong(third_party_service_calls);
    }
    writer.endObject();

    long totalStorageByteHours = storageBytes + unbilledResources.storage;
    long totalBandwidth = bandwidth + unbilledResources.bandwidth;
    long totalFirstPartyCalls = first_party_service_calls + unbilledResources.first_party_service_calls;
    long totalThirdPartyCalls = third_party_service_calls + unbilledResources.third_party_service_calls;

    int brickPennies = (int) Math.ceil(max(//
        messages / rates.messages, //
        count / rates.count, //
        memory / rates.memory, //
        connections / rates.connections, //
        cpuTicks / rates.cpu)); //
    int storagePennies = (int) (totalStorageByteHours / rates.storage);
    int bandwidthPennies = (int) (totalBandwidth / rates.bandwidth);
    int firstPartyPennies = (int) (totalFirstPartyCalls / rates.first_party_service_calls);
    int thirdPartyPennies = (int) (totalThirdPartyCalls / rates.third_party_service_calls);
    int pennies = brickPennies + storagePennies + bandwidthPennies + firstPartyPennies + thirdPartyPennies;
    UnbilledResources change = new UnbilledResources(
        (totalStorageByteHours % rates.storage) - unbilledResources.storage,
        (totalBandwidth % rates.bandwidth) - unbilledResources.bandwidth,
        (totalFirstPartyCalls % rates.first_party_service_calls) - unbilledResources.first_party_service_calls,
        (totalThirdPartyCalls % rates.third_party_service_calls) - unbilledResources.third_party_service_calls);
    return new MeteredWindowSummary(writer.toString(), pennies, storageBytes, change);
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
