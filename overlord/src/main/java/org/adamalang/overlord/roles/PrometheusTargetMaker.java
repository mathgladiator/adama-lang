/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.overlord.roles;

import com.fasterxml.jackson.databind.json.JsonMapper;
import org.adamalang.gossip.Engine;
import org.adamalang.gossip.proto.Endpoint;
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

public class PrometheusTargetMaker {
  private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusTargetMaker.class);

  /** scan gossip table to make targets.json for promethesus */
  public static void kickOff(OverlordMetrics metrics, Engine engine, File targetsDestination, ConcurrentCachedHttpHandler handler) {
    AtomicReference<String> lastWritten = new AtomicReference<>("");
    HashMap<String, Endpoint> cached = new HashMap<>();
    engine.setWatcher((endpointsLatest) -> {
      // this is a hack to keep everything available so things just don't go pop due to a gossip failure
      // TODO: add a system here to keep an endpoint for at least an hour for monitoring
      for (Endpoint endpoint : endpointsLatest) {
        cached.put(endpoint.getIp() + ":" + endpoint.getPort(), endpoint);
      }
      metrics.targets_watcher_fired.run();
      HashSet<String> seen = new HashSet<>();
      JsonStreamWriter writer = new JsonStreamWriter();
      writer.beginArray();
      for (Endpoint endpoint : cached.values()) {
        if (endpoint.getMonitoringPort() >= 0) {
          String target = endpoint.getIp() + ":" + endpoint.getMonitoringPort();
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
              writer.writeString(classifyByPort(endpoint.getMonitoringPort()));
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
