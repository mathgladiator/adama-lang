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
package org.adamalang.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.web.io.ConnectionContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/** stats for the devbox */
public class DevBoxStats {
  private final AtomicInteger lintIssues;
  private final AtomicInteger backendDeployments;
  private final AtomicInteger frontendDeployments;
  private final AtomicInteger payloads;
  private final AtomicInteger testFailures;
  private final AtomicLong bytes;
  private final AtomicInteger frontendByteSize;
  private final AtomicReference<ObjectNode> metrics;
  private final ConcurrentHashMap<String, ConcurrentHashMap<String, AtomicInteger>> payload_shred;

  public DevBoxStats() {
    lintIssues = new AtomicInteger(0);
    backendDeployments = new AtomicInteger(0);
    frontendDeployments = new AtomicInteger(0);
    payloads = new AtomicInteger(0);
    testFailures = new AtomicInteger(0);
    bytes = new AtomicLong(0);
    frontendByteSize = new AtomicInteger(0);
    metrics = new AtomicReference<>(Json.newJsonObject());
    payload_shred = new ConcurrentHashMap<>();
  }

  public void lintIssues(String space, int count) {
    lintIssues.addAndGet(count);
  }

  public void backendDeployment(String space) {
    backendDeployments.incrementAndGet();
  }

  public void frontendDeployment(String forest) {
    frontendDeployments.incrementAndGet();
  }

  public void frontendSize(int size) {
    frontendByteSize.set(size);
  }

  public void payload(String space, String key, String payload) {
    bytes.addAndGet(payload.length());
    payloads.incrementAndGet();
    // shred the payload
    ConcurrentHashMap<String, AtomicInteger> shred = payload_shred.get(space + "/" + key);
    if (shred == null) {
      shred = new ConcurrentHashMap<>();
      payload_shred.put(space + "/" + key, shred);
    }
    JsonNode parsed = Json.parse(payload);
    if (parsed.has("data")) {
      parsed = parsed.get("data");
    }
    Iterator<Map.Entry<String, JsonNode>> it = parsed.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> entry = it.next();
      AtomicInteger sum = shred.get(entry.getKey());
      if (sum == null) {
        sum = new AtomicInteger();
        shred.put(entry.getKey(), sum);
      }
      sum.addAndGet(entry.getValue().toString().length());
    }
  }

  public void testFailures(String space, int failures) {
    testFailures.addAndGet(failures);
  }

  public void metrics(ObjectNode metrics) {
    this.metrics.set(metrics);
  }

  public void reset() {
    lintIssues.set(0);
    backendDeployments.set(0);
    frontendDeployments.set(0);
    payloads.set(0);
    bytes.set(0);
    testFailures.set(0);
    frontendByteSize.set(0);
    metrics.set(Json.newJsonObject());
    payload_shred.clear();
  }

  public String dumpHTML(ConnectionContext context) {
    StringBuilder sb = new StringBuilder();
    sb.append("<html><head><title>Adama DevBox Stats</title><body>");
    sb.append("origin:").append(context.origin).append(", remoteIp:").append(context.remoteIp).append(", user-agent").append(context.userAgent).append("<hr/>\n");
    sb.append("<ul>\n");
    writeSimple(sb, "lint-issues", backendDeployments);
    writeSimple(sb, "backend-deployments", backendDeployments);
    writeSimple(sb, "frontend-deployments", frontendDeployments);
    writeSimple(sb, "frontend-size", frontendByteSize);
    writeSimple(sb, "payloads", payloads);
    writeSimple(sb, "bytes", bytes);
    writeSimple(sb, "test-failures", testFailures);
    sb.append("</ul>\n");
    sb.append("<h1>Metrics</h1>\n");
    sb.append("<pre>").append(metrics.get().toPrettyString()).append("</pre>");
    sb.append("<h1>Payload Shred</h1>\n");

    for (Map.Entry<String, ConcurrentHashMap<String, AtomicInteger>> pick : payload_shred.entrySet()) {
      sb.append("<h2>").append(pick.getKey()).append("</h2><br/>\n");
      ArrayList<Map.Entry<String, AtomicInteger>> entries = new ArrayList<>(pick.getValue().entrySet());
      entries.sort(Comparator.comparingInt(a -> -a.getValue().get()));
      long total = 0;
      for (Map.Entry<String, AtomicInteger> entry : entries) {
        total += entry.getValue().get();
      }
      for (Map.Entry<String, AtomicInteger> entry : entries) {
        int bytes = entry.getValue().get();
        if (bytes > 1024 * 64) {
          sb.append("<b>");
        }
        sb.append(entry.getKey()).append(" = ").append(bytes);
        double percent = Math.round(1000.0 * entry.getValue().get() / total) / 10.0;
        sb.append(" (").append(percent).append("%)");
        if (bytes > 1024 * 64) {
          sb.append("</b>");
        }
        sb.append("<br />");
      }
    }
    sb.append("</body></html>");
    return sb.toString();
  }

  private void writeSimple(StringBuilder sb, String name, AtomicInteger v) {
    sb.append("  <li>").append(name).append(" = ").append(v.get()).append("<br />\n");
  }

  private void writeSimple(StringBuilder sb, String name, AtomicLong v) {
    sb.append("  <li>").append(name).append(" = ").append(v.get()).append("<br />\n");
  }
}
