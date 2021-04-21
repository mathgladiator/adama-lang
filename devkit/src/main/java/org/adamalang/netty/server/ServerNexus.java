/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.server;

import org.adamalang.netty.api.GameSpaceDB;
import org.adamalang.netty.contracts.Authenticator;
import org.adamalang.netty.contracts.JsonHandler;
import org.adamalang.netty.contracts.ServerOptions;
import org.adamalang.netty.contracts.StaticSite;
import org.adamalang.runtime.contracts.AssetService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ServerNexus {
  public final Authenticator authenticator;
  public final GameSpaceDB db;
  public final JsonHandler handler;
  public final ServerOptions options;
  public final StaticSite site;
  public final AssetService assetService;
  public final ScheduledExecutorService heartbeat;

  public ServerNexus(final ServerOptions options, final GameSpaceDB db, final JsonHandler handler, final Authenticator authenticator, final StaticSite site, final AssetService assetService) {
    this.options = options;
    this.db = db;
    this.handler = handler;
    this.authenticator = authenticator;
    this.site = site;
    this.assetService = assetService;
    this.heartbeat = Executors.newSingleThreadScheduledExecutor();
  }

  public void shutdown() throws Exception {
    handler.shutdown();
    db.close();
    heartbeat.shutdown();
  }
}
