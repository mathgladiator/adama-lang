package org.adamalang.net.server;

import org.adamalang.common.ExceptionSupplier;
import org.adamalang.common.net.ByteStream;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Server implements AutoCloseable {
  private final Supplier<Runnable> cancelServerSupplier;
  private final AtomicBoolean alive;
  private Runnable cancelServer;

  public Server(ServerNexus nexus) throws Exception {
    this.alive = new AtomicBoolean(false);
    this.cancelServer = null;
    org.adamalang.common.net.Server server = new org.adamalang.common.net.Server( );
    this.cancelServerSupplier = ExceptionSupplier.TO_RUNTIME(() -> {
      return server.start(nexus.base, nexus.identity, nexus.port, upstream -> new Handler(upstream));
    });
  }

  /** Start serving requests. */
  public synchronized void start() throws IOException {
    if (alive.compareAndExchange(false, true) == false) {
      cancelServer = cancelServerSupplier.get();
    }
  }

  /** Finish serving request */
  @Override
  public synchronized void close() throws InterruptedException {
    if (alive.compareAndExchange(true, false) == true) {
      cancelServer.run();
    }
  }
}
