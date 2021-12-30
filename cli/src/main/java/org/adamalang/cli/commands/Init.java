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

import java.util.logging.ConsoleHandler;

public class Init {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      initHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "start":
        initStart(config);
        return;
      case "revoke":
        initRevoke(config);
        return;
      case "help":
        initHelp();
        return;
    }
  }

  private static String readEmail(Config config) {
    System.out.println();
    System.out.print(Util.prefix("   Email:", Util.ANSI.Yellow));
    String email = System.console().readLine();
    // TODO: rough validate email
    // TODO: if blank, then try to pull from config
    return email;
  }

  private static void initStart(Config config) throws Exception {
    String email = readEmail(config);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode requestStart = Json.newJsonObject();
        requestStart.put("method", "init/start");
        requestStart.put("email", email);
        long initConnectionId = connection.open(requestStart, (o) -> {

        }, (ex) -> {});

        System.out.println();
        System.out.print(Util.prefix("    Code:", Util.ANSI.Yellow));
        String code = System.console().readLine();

        ObjectNode requestGenerateIdentity = Json.newJsonObject();
        requestGenerateIdentity.put("method", "init/generate-identity");
        requestGenerateIdentity.put("connection", initConnectionId);
        requestGenerateIdentity.put("code", code);
        ObjectNode responseGenerateIdentity = connection.execute(requestGenerateIdentity);
        config.manipulate((node) -> {
          node.put("email", email);
          node.set("identity", responseGenerateIdentity.get("identity"));
        });
      }
    }
  }

  private static void initRevoke(Config config) throws Exception {
    String email = readEmail(config);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode requestStart = Json.newJsonObject();
        requestStart.put("method", "init/start");
        requestStart.put("email", email);
        long initConnectionId = connection.open(requestStart, (o) -> {

        }, (ex) -> {});

        System.out.println();
        System.out.print(Util.prefix("    Code:", Util.ANSI.Yellow));
        String code = System.console().readLine();

        ObjectNode requestRevoke = Json.newJsonObject();
        requestRevoke.put("method", "init/revoke-all");
        requestRevoke.put("connection", initConnectionId);
        requestRevoke.put("code", code);
        ObjectNode responseRevoke = connection.execute(requestRevoke);
        System.err.println(responseRevoke.toPrettyString());

        config.manipulate((node) -> {
          node.remove("email");
          node.remove("identity");
        });
      }
    }
  }

  public static void initHelp() {
    System.out.println(Util.prefix("Initiate the local configuration file", Util.ANSI.Green));
    System.out.println("");
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama code", Util.ANSI.Green) + " " + Util.prefix("[INITSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println("");
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println("");
    System.out.println(Util.prefix("INITSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("start", Util.ANSI.Green) + "             Connect this machine to the remote resource (interactive)");
    System.out.println("    " + Util.prefix("revoke", Util.ANSI.Green) + "            Revoke all machines for your account (interactive)");
  }
}
