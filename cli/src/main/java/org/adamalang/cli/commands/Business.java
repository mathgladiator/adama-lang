/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli.commands;

import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.DataBaseConfig;
import org.adamalang.mysql.DataBaseMetrics;
import org.adamalang.mysql.frontend.Users;

public class Business {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      businessHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "add-balance":
        addBalance(config, next);
        return;
      case "help":
        businessHelp();
        return;
    }
  }

  public static void addBalance(Config config, String[] args) throws Exception {
    String email = Util.extractOrCrash("--email", "-e", args);
    int change = Integer.parseInt(Util.extractOrCrash("--pennies", "-p", args));
    DataBase db = new DataBase(new DataBaseConfig(new ConfigObject(config.read()), "frontend"), new DataBaseMetrics(new NoOpMetricsFactory(), "noop"));
    int userId = Users.getOrCreateUserId(db, email);
    System.out.println("Balance Before: " + Util.prefix("" + Users.getBalance(db, userId), Util.ANSI.Green));
    Users.addToBalance(db, userId, change);
    System.out.println("Balance After: " + Util.prefix("" + Users.getBalance(db, userId), Util.ANSI.Green));
  }

  public static void businessHelp() {
    System.out.println(Util.prefix("Business Support.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama business", Util.ANSI.Green) + " " + Util.prefix("[BUSINESSSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("BUSINESSSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("add-balance", Util.ANSI.Green) + "       Interactive setup for the config");
  }
}
