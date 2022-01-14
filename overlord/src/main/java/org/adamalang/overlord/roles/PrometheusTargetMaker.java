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

import org.adamalang.gossip.Engine;
import org.adamalang.gossip.proto.Endpoint;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class PrometheusTargetMaker {
  private static Logger LOGGER = LoggerFactory.getLogger(PrometheusTargetMaker.class);

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

  /** scan gossip table to make targets.json for promethesus */
  public static void kickOff(Engine engine, File targetsDestination) {
    AtomicReference<String> lastWritten = new AtomicReference<>("");
    engine.setWatcher(
        (endpoints) -> {
          HashSet<String> seen = new HashSet<>();

          JsonStreamWriter writer = new JsonStreamWriter();
          writer.beginArray();
          for (Endpoint endpoint : endpoints) {
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
            if (!lastWritten.get().equals(toWrite)) {
              Files.writeString(targetsDestination.toPath(), writer.toString());
              lastWritten.set(toWrite);
            }
          } catch (Exception ex) {
            LOGGER.error("failed-write-to-disk", ex);
          }
        });
  }
}
