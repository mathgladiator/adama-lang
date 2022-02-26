/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli;

import org.adamalang.cli.commands.*;

public class Main {

  public static void main(String[] preFilteredArgs) throws Exception {
    Config config = new Config(preFilteredArgs);
    if (preFilteredArgs.length == 0) {
      rootHelp();
      return;
    }
    String[] args = config.argsForTool;
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "authority":
        Authority.execute(config, next);
        return;
      case "aws":
        AWS.execute(config, next);
        return;
      case "business":
        Business.execute(config, next);
        return;
      case "code":
        Code.execute(config, next);
        return;
      case "contrib":
        Contrib.execute(config, next);
        return;
      case "database":
        Database.execute(config, next);
        return;
      case "documents":
      case "document":
        Documents.execute(config, next);
        return;
      case "fleet":
        Fleet.execute(config, next);
        return;
      case "init":
        Init.execute(config, next);
        return;
      case "security":
        Security.execute(config, next);
        return;
      case "service":
        Service.execute(config, next);
        return;
      case "space":
      case "spaces":
        Space.execute(config, next);
        return;
      case "stress":
        Stress.execute(config, next);
        return;
      case "help":
        rootHelp();
        return;
    }
  }

  public static void rootHelp() {
    System.out.println(Util.prefix("Interacts with the Adama Platform", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama", Util.ANSI.Green) + " " + Util.prefix("[SUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SUBCOMMANDS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("authority", Util.ANSI.Green) + "         Manage authorities");
    System.out.println("    " + Util.prefix("aws", Util.ANSI.Green) + "               Tools for working with AWS");
    System.out.println("    " + Util.prefix("business", Util.ANSI.Green) + "          Business tools to support developers");
    System.out.println("    " + Util.prefix("code", Util.ANSI.Green) + "              Local developer tools");
    System.out.println("    " + Util.prefix("contrib", Util.ANSI.Green) + "           Open source contributor tools");
    System.out.println("    " + Util.prefix("database", Util.ANSI.Green) + "          Prepare database for usage");
    System.out.println("    " + Util.prefix("document", Util.ANSI.Green) + "          Interact with documents");
    System.out.println("    " + Util.prefix("fleet", Util.ANSI.Green) + "             Fleet management via EC2");
    System.out.println("    " + Util.prefix("init", Util.ANSI.Green) + "              Initializes the config with a valid token");
    System.out.println("    " + Util.prefix("security", Util.ANSI.Green) + "          Security tools for production usage");
    System.out.println("    " + Util.prefix("service", Util.ANSI.Green) + "           Launch a service");
    System.out.println("    " + Util.prefix("space", Util.ANSI.Green) + "             Manages spaces");
    System.out.println("    " + Util.prefix("stress", Util.ANSI.Green) + "            Stress test using the canary tool");
  }
}
