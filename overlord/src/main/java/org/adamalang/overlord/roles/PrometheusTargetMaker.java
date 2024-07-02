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
package org.adamalang.overlord.roles;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.adamalang.common.gossip.Engine;
import org.adamalang.common.gossip.codec.GossipProtocol;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

/** convert the local gossip table into a targets table for prometheus */
public class PrometheusTargetMaker {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusTargetMaker.class);

  /** scan gossip table to make targets.json for promethesus */
  public static void kickOff(OverlordMetrics metrics, Engine engine, File targetsDestination, ConcurrentCachedHttpHandler handler) {
    AtomicReference<String> lastWritten = new AtomicReference<>("");
    HashMap<String, GossipProtocol.Endpoint> cached = new HashMap<>();
    engine.setWatcher((endpoints) -> {
      // this is a hack to keep everything available so things just don't go pop due to a gossip failure
      // TODO: add a system here to keep an endpoint for at least an hour for monitoring
      for (GossipProtocol.Endpoint endpoint : endpoints) {
        cached.put(endpoint.ip + ":" + endpoint.port, endpoint);
      }
      metrics.targets_watcher_fired.run();
      int backendHosts = 0;
      HashSet<String> seen = new HashSet<>();
      JsonStreamWriter writer = new JsonStreamWriter();
      writer.beginArray();
      for (GossipProtocol.Endpoint endpoint : cached.values()) {
        if (endpoint.monitoringPort >= 0) {
          String target = endpoint.ip + ":" + endpoint.monitoringPort;
          if (seen.contains(target)) {
            continue;
          }
          seen.add(target);
          {
            writer.beginObject();
            writer.writeObjectFieldIntro("labels");
            {
              writer.beginObject();
              writer.writeObjectFieldIntro("service");
              String service = classifyByPort(endpoint.monitoringPort);
              if ("backend".equals(service)) {
                backendHosts++;
              }
              writer.writeString(service);
              writer.endObject();
            }
            writer.writeObjectFieldIntro("targets");
            {
              writer.beginArray();
              writer.writeString(target);
              writer.endArray();
            }
            writer.endObject();
          }
        }
      }
      writer.endArray();
      if (backendHosts == 0) {
        metrics.targets_scan_zero_backend.set(1);
        LOGGER.error("zero back-end hosts detected; aborting targets");
        return;
      } else {
        metrics.targets_scan_zero_backend.set(0);
      }
      try {
        String toWrite = writer.toString();
        handler.put("/targets", "<html><head><title>Targets</title></head><body><pre>" + JsonMapper.builder().build().readTree(toWrite).toPrettyString() + "</pre></body></html>");
        if (!lastWritten.get().equals(toWrite)) {
          LOGGER.info("made-targets", toWrite);
          Files.writeString(targetsDestination.toPath(), writer.toString());
          metrics.targets_made.run();
          lastWritten.set(toWrite);
        } else {
          metrics.targets_skipped.run();
        }
      } catch (Exception ex) {
        LOGGER.error("failed-write-to-disk", ex);
      }
    });
  }

  private static String classifyByPort(int port) {
    switch (port) {
      case 8003:
        return "backend";
      case 8005:
        return "frontend";
      case 8011:
        return "overlord";
      default:
        return "unknown";
    }
  }
}
