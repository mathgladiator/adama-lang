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

import org.adamalang.common.NamedRunnable;
import org.adamalang.overlord.OverlordMetrics;
import org.adamalang.overlord.grpc.OverlordServer;
import org.adamalang.overlord.html.ConcurrentCachedHttpHandler;
import org.adamalang.overlord.html.FixedHtmlStringLoggerTable;

import java.io.File;

public class WebRootScanner {

  public static void scan(File root, String uri, FixedHtmlStringLoggerTable logger) {
    if (!root.exists() || !root.isDirectory()) {
      logger.row("scan:" + uri, "not-exists-nor-directory");
      return;
    }
    for (File child : root.listFiles()) {
      if (child.isDirectory()) {
        scan(child, uri + child.getName() + "/", logger);
      } else {
        logger.row("witness:" + uri, "ok");
      }
    }
  }

  public static void kickOff(OverlordMetrics metrics, OverlordServer server, ConcurrentCachedHttpHandler hander, String scanPath) {
    FixedHtmlStringLoggerTable logger = new FixedHtmlStringLoggerTable(1, "action", "result");
    server.executor.schedule(new NamedRunnable("scan-web-root") {
      @Override
      public void execute() throws Exception {
        try {
          scan(new File(scanPath), "/", logger);
        } finally {
          server.executor.schedule(this, (int) (5000 + 5000 * Math.random()));
          hander.put("/webroot", logger.toHtml("Web Root Scanner Results"));
        }
      }
    }, 500);
  }
}
