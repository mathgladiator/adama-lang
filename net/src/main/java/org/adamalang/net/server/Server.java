/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.server;

import org.adamalang.common.ExceptionSupplier;

import java.util.function.Supplier;

public class Server {
  private final Supplier<Runnable> serverThreadSupplier;

  public Server(ServerNexus nexus) throws Exception {
    org.adamalang.common.net.Server server = new org.adamalang.common.net.Server( );
    this.serverThreadSupplier = ExceptionSupplier.TO_RUNTIME(() -> {
      return server.start(nexus.base, nexus.port, upstream -> new Handler(nexus, upstream));
    });
  }

  /** Start serving requests. */
  public void start() {
      new Thread(serverThreadSupplier.get()).start();
  }
}
