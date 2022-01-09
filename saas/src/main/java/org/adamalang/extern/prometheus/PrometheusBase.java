/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.extern.prometheus;

import io.prometheus.client.exporter.HTTPServer;
import io.prometheus.client.hotspot.GarbageCollectorExports;
import io.prometheus.client.hotspot.MemoryPoolsExports;

public class PrometheusBase {
  private final HTTPServer server;
  public PrometheusBase(int metricsHttpPort) throws Exception {
    server = new HTTPServer.Builder()
        .withPort(metricsHttpPort)
        .build();
    new MemoryPoolsExports().register();
    new GarbageCollectorExports().register();
  }

  public void shutdown() {
    server.close();
  }

  public static void main(String[] args) throws Exception {
    new PrometheusBase(9000);
  }
}
