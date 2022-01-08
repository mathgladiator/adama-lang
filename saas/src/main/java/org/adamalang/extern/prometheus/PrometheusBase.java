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
