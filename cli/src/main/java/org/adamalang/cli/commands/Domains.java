/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.commands.frontend.FrontendDeveloperServer;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Json;
import org.adamalang.rxhtml.RxHtmlTool;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class Domains {

  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      domainsHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "map":
        map(config, next);
        return;
      case "list":
        list(config, next);
        return;
      case "unmap":
        unmap(config, next);
        return;
      case "help":
      default:
        domainsHelp();
    }
  }

  public static void domainsHelp() {
    System.out.println(Util.prefix("Tools to help with frontend.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama domains", Util.ANSI.Green) + " " + Util.prefix("[DOMAINSSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("DOMAINSSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("map", Util.ANSI.Green) + "               Map a domain to a space");
    System.out.println("    " + Util.prefix("list", Util.ANSI.Green) + "              List domains");
    System.out.println("    " + Util.prefix("unmap", Util.ANSI.Green) + "             Unmap a domain from a space");
  }

  public static void map(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String domain = Util.extractOrCrash("--domain", "-d", args);
    String space = Util.extractOrCrash("--space", "-s", args);
    String autoStr = Util.extractWithDefault("--auto", "-a", "true", args).toLowerCase();
    boolean automatic = "true".equals(autoStr) || "yes".equals(autoStr);
    final String cert;
    if (!automatic) {
      String certFile = Util.extractOrCrash("--cert", "-c", args);
      cert = Files.readString(new File(certFile).toPath());
    } else {
      cert = null;
    }
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "domain/map");
        request.put("identity", identity);
        request.put("domain", domain);
        request.put("space", space);
        if (cert != null) {
          request.put("certificate", cert);
        }
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  public static void list(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "domain/list");
        request.put("identity", identity);
        connection.stream(request, (_k, item) -> {
          System.err.println(item.toPrettyString());
        });
      }
    }
  }

  public static void unmap(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String domain = Util.extractOrCrash("--domain", "-d", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "domain/unmap");
        request.put("identity", identity);
        request.put("domain", domain);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }
}
