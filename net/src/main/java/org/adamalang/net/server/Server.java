package org.adamalang.net.server;

import org.adamalang.common.ExceptionSupplier;

import java.io.IOException;
import java.util.function.Supplier;

public class Server {
  private final Supplier<Runnable> serverThreadSupplier;

  public Server(ServerNexus nexus) throws Exception {
    org.adamalang.common.net.Server server = new org.adamalang.common.net.Server( );
    this.serverThreadSupplier = ExceptionSupplier.TO_RUNTIME(() -> {
      return server.start(nexus.base, nexus.identity, nexus.port, upstream -> new Handler(upstream));
    });
  }

  /** Start serving requests. */
  public void start() {
      new Thread(serverThreadSupplier.get()).start();
  }
}
