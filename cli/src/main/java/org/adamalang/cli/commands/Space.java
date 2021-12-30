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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Json;
import org.adamalang.common.Validators;

public class Space {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
        spaceHelp(args);
        return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "create":
        spaceCreate(config, next);
        return;
      case "deploy":
        spaceDeploy(config, next);
        return;
      case "download":
        spaceDownload(config, next);
        return;
      case "list":
        spaceList(config, next);
        return;
      case "set-role":
        spaceSetRole(config, next);
        return;
      case "help":
        spaceHelp(next);
        return;
    }
  }

  private static void spaceCreate(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    if (!Validators.simple(space, 127)) {
      System.err.println("Space name `" + space + "` is not valid");
      return;
    }
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/create");
        request.put("identity", identity);
        request.put("space", space);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  private static void spaceDeploy(Config config, String[] args) throws Exception {
    // TODO: (1) search for --file OR --plan, (2.a) for --file, build a dumb plan and compile the file for fast, (2.b) for --plan validate the plan json, (3) send the plan to socket
  }

  private static void spaceDownload(Config config, String[] args) throws Exception {
    // TODO: (1) validate next[0] exists and then go forth and fetch the plan and return a pretty printed version
  }

  private static void spaceList(Config config, String[] args) throws Exception {
    // TODO: (1) search for marker in next, (2) search for limit in next, (3) send the command along, (4) return beautiful results
  }

  private static void spaceSetRole(Config config, String[] args) throws Exception {
    // TODO: (1) search for space, (2) search for email, (3) search for role
  }

  public static void spaceHelp(String[] args) {
    if (args.length > 0) {
      String command = Util.normalize(args[0]);
    }
    System.out.println(Util.prefix("Adama organizes documents into spaces, and spaces can be managed with this tool.", Util.ANSI.Green));
    System.out.println("");
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama space", Util.ANSI.Green) + " " + Util.prefix("[SPACESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println("");
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println("");
    System.out.println(Util.prefix("SPACESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("create", Util.ANSI.Green) + "            Create a new space");
    System.out.println("    " + Util.prefix("deploy", Util.ANSI.Green) + "            Deploy a plan to a space");
    System.out.println("    " + Util.prefix("download", Util.ANSI.Green) + "          Download a space's plan");
    System.out.println("    " + Util.prefix("list", Util.ANSI.Green) + "              List spaces available to your account");
    System.out.println("    " + Util.prefix("set-role", Util.ANSI.Green) + "          Share/unshare a space with another developer");
    System.out.println("    " + Util.prefix("help", Util.ANSI.Green) + "              Show this helpful message");
  }
}
