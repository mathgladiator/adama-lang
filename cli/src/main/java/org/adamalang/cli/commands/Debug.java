/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.commands;

import io.netty.buffer.Unpooled;
import org.adamalang.caravan.events.*;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.model.Users;

import java.io.File;
import java.util.ArrayList;

public class Debug {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      debugHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "archive":
        debugArchive(config, next);
        return;
      case "help":
        debugHelp();
        return;
    }
  }

  public static void debugArchive(Config config, String[] args) throws Exception {
    String space = Util.extractOrCrash("--space", "-k", args);
    String archive = Util.extractOrCrash("--archive", "-k", args);
    ArrayList<byte[]> writes = RestoreLoader.load(new File("archive/" + space + "/" + archive));
    System.err.println("Restore Log");
    RestoreDebuggerStdErr.print(writes);
    System.err.println("Live Asset Ids:");
    for (String id : AssetWalker.idsOf(writes)) {
      System.err.println(id);
    }

  }

  public static void debugHelp() {
    System.out.println(Util.prefix("Operational Support.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama debug", Util.ANSI.Green) + " " + Util.prefix("[DEBUGSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("DEBUGSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("archive", Util.ANSI.Green) + "           Explain the data within an archive");
  }
}
