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
package org.adamalang.runtime.sys;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.LogTimestamp;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/** track specific events */
public class PerfTracker {
  private static final Logger LOG = LoggerFactory.getLogger("tracked");
  private final HashMap<String, ArrayList<Sample>> samples;
  private final LivingDocument owner;
  private boolean lightning;
  private ObjectNode lightningGraph;
  private Stack<ObjectNode> current;

  public PerfTracker(LivingDocument owner) {
    this.owner = owner;
    this.samples = new HashMap<>();
    this.lightning = false;
  }

  public void measureLightning() {
    this.lightning = true;
    this.lightningGraph = Json.newJsonObject();
    this.current = new Stack<>();
    this.current.push(lightningGraph);
  }

  public String getLightningJsonAndReset() {
    if (this.lightning) {
      String result = lightningGraph.toString();
      lightningGraph.removeAll();
      return result;
    }
    return null;
  }

  private Runnable measureWithLightning(String name) {
    Runnable base = measureNoLightning(name);
    ObjectNode head = current.peek();
    ObjectNode path = head.has(name) ? (ObjectNode) head.get(name) : head.putObject(name);
    long started = System.currentTimeMillis();
    current.push(path);
    return () -> {
      long ms = System.currentTimeMillis() - started;
      if (path.has("__ms")) {
        ms += path.get("__ms").longValue();
      }
      path.put("__ms", ms);
      base.run();
      current.pop();
    };
  }

  private Runnable measureNoLightning(String name) {
    ArrayList<Sample> _sample = samples.get(name);
    if (_sample == null) {
      _sample = new ArrayList<>();
      samples.put(name, _sample);
    }
    final ArrayList<Sample> sample = _sample;
    long timeStart = System.currentTimeMillis();
    int costStart = owner.__getCodeCost();
    return () -> {
      sample.add(new Sample(owner.__getCodeCost() - costStart, System.currentTimeMillis() - timeStart));
    };
  }

  public Runnable measure(String name) {
    if (lightning) {
      return measureWithLightning(name);
    } else {
      return measureNoLightning(name);
    }
  }

  public static void writeDeploymentTime(String space, long latency, boolean success) {
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("@timestamp");
    writer.writeString(LogTimestamp.now());
    writer.writeObjectFieldIntro("type");
    writer.writeString("deployment");
    writer.writeObjectFieldIntro("space");
    writer.writeString(space);
    writer.writeObjectFieldIntro("latency");
    writer.writeLong(latency);
    writer.writeObjectFieldIntro("success");
    writer.writeBoolean(success);
    writer.endObject();
    String result = writer.toString();
    LOG.error(result);
  }

  public String dump(double threshold) {
    if (samples.isEmpty()) {
      return null;
    }
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.beginObject();
    writer.writeObjectFieldIntro("@timestamp");
    writer.writeString(LogTimestamp.now());
    writer.writeObjectFieldIntro("type");
    writer.writeString("document");
    writer.writeObjectFieldIntro("space");
    writer.writeString(owner.__getSpace());
    writer.writeObjectFieldIntro("key");
    writer.writeString(owner.__getKey());
    writer.writeObjectFieldIntro("values");
    writer.beginObject();
    for (Map.Entry<String, ArrayList<Sample>> entry : samples.entrySet()) {
      ArrayList<Sample> samples = entry.getValue();
      int n = samples.size();
      long sum_cost = 0;
      double sum_ms = 0.0;
      double min_ms = 60000;
      double max_ms = 0.0;
      for (Sample sample : samples) {
        sum_cost += sample.cost;
        sum_ms += sample.ms;
        if (sample.ms > max_ms) {
          max_ms = sample.ms;
        }
        if (sample.ms < min_ms) {
          min_ms = sample.ms;
        }
      }
      if (max_ms > threshold) {
        writer.writeObjectFieldIntro(entry.getKey());
        writer.beginObject();
        writer.writeObjectFieldIntro("n");
        writer.writeInteger(n);
        writer.writeObjectFieldIntro("avg_cost");
        writer.writeDouble(sum_cost / (double) n);
        writer.writeObjectFieldIntro("avg_ms");
        writer.writeDouble(sum_ms / (double) n);
        writer.writeObjectFieldIntro("sum_ms");
        writer.writeDouble(sum_ms);
        writer.writeObjectFieldIntro("min_ms");
        writer.writeDouble(min_ms);
        writer.writeObjectFieldIntro("max_ms");
        writer.writeDouble(max_ms);
        writer.endObject();
      }
    }
    writer.endObject();
    writer.endObject();
    samples.clear();
    String result = writer.toString();
    LOG.error(result);
    return result;
  }

  public class Sample {
    private final int cost;
    private final double ms;

    public Sample(int cost, double ms) {
      this.cost = cost;
      this.ms = ms;
    }
  }
}
