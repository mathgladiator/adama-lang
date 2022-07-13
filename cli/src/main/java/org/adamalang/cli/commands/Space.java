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
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.remote.Deliverer;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

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
      case "delete":
        spaceDelete(config, next);
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
      case "reflect":
        spaceReflect(config, next);
        return;
      case "set-role":
        spaceSetRole(config, next);
        return;
      case "usage":
        spaceUsage(config, next);
        return;
      case "help":
        spaceHelp(next);
        return;
    }
  }

  public static void spaceHelp(String[] args) {
    if (args.length > 0) {
      String command = Util.normalize(args[0]);
    }
    System.out.println(Util.prefix("Adama organizes documents into spaces, and spaces can be managed with this tool.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama space", Util.ANSI.Green) + " " + Util.prefix("[SPACESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SPACESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("usage", Util.ANSI.Green) + "             Iterate the billed usage");
    System.out.println("    " + Util.prefix("create", Util.ANSI.Green) + "            Create a new space");
    System.out.println("    " + Util.prefix("delete", Util.ANSI.Green) + "            Deletes an empty space");
    System.out.println("    " + Util.prefix("deploy", Util.ANSI.Green) + "            Deploy a plan to a space");
    System.out.println("    " + Util.prefix("download", Util.ANSI.Green) + "          Download a space's plan");
    System.out.println("    " + Util.prefix("list", Util.ANSI.Green) + "              List spaces available to your account");
    System.out.println("    " + Util.prefix("set-role", Util.ANSI.Green) + "          Share/unshare a space with another developer");
    System.out.println("    " + Util.prefix("help", Util.ANSI.Green) + "              Show this helpful message");
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

  private static void spaceDelete(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/delete");
        request.put("identity", identity);
        request.put("space", space);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  private static void spaceDeploy(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String singleFile = Util.extractWithDefault("--file", "-f", null, args);
    final String planJson;
    if (singleFile != null) {
      System.out.println("validating file...");
      if (!Code.compileFile(config, args)) {
        return;
      }
      String singleScript = Files.readString(new File(singleFile).toPath());
      ObjectNode plan = Json.newJsonObject();
      plan.putObject("versions").put("file", singleScript);
      plan.put("default", "file");
      plan.putArray("plan");
      planJson = plan.toString();
    } else {
      System.out.println("validating plan...");
      if (!Code.validatePlan(config, args)) {
        return;
      }
      String planFile = Util.extractOrCrash("--plan", "-p", args);
      planJson = Files.readString(new File(planFile).toPath());
    }
    DeploymentPlan localPlan = new DeploymentPlan(planJson, (t, c) -> t.printStackTrace());
    System.out.println("final check of plan...");
    String spacePrefix = DeploymentFactoryBase.getSpaceClassNamePrefix(space);
    new DeploymentFactory(space, spacePrefix, new AtomicInteger(0), null, localPlan, Deliverer.FAILURE);
    System.out.println("deploying plan...");

    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/set");
        request.put("identity", identity);
        request.put("space", space);
        request.set("plan", Json.parseJsonObject(planJson));
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  private static void spaceDownload(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/get");
        request.put("identity", identity);
        request.put("space", space);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  private static void spaceList(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);

    String marker = Util.extractWithDefault("--marker", "-m", "", args);
    int limit = Integer.parseInt(Util.extractWithDefault("--limit", "-l", "100", args));

    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/list");
        request.put("identity", identity);
        if (!"".equals(marker)) {
          request.put("marker", marker);
        }
        request.put("limit", limit);
        connection.stream(request, (cId, response) -> {
          System.err.println(response.toPrettyString());
        });
      }
    }
  }

  private static void spaceUsage(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    int limit = Integer.parseInt(Util.extractWithDefault("--limit", "-l", "336", args));

    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/usage");
        request.put("identity", identity);
        request.put("space", space);
        request.put("limit", limit);
        connection.stream(request, (cId, response) -> {
          System.err.println(response.toPrettyString());
        });
      }
    }
  }

  private static void spaceReflect(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractWithDefault("--key", "-k", "", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/reflect");
        request.put("identity", identity);
        request.put("space", space);
        request.put("key", key);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  private static void spaceSetRole(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String email = Util.extractWithDefault("--email", "-e", "", args);
    String role = Util.extractWithDefault("--role", "-r", "none", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/set-role");
        request.put("identity", identity);
        request.put("space", space);
        request.put("email", email);
        request.put("role", role);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }
}
