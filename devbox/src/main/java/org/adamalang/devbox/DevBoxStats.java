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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.web.io.ConnectionContext;

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

  public DevBoxStats() {
    lintIssues = new AtomicInteger(0);
    backendDeployments = new AtomicInteger(0);
    frontendDeployments = new AtomicInteger(0);
    payloads = new AtomicInteger(0);
    testFailures = new AtomicInteger(0);
    bytes = new AtomicLong(0);
    frontendByteSize = new AtomicInteger(0);
    metrics = new AtomicReference<>(Json.newJsonObject());
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
    sb.append("<pre>").append(metrics.get().toPrettyString()).append("</pre>");
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
