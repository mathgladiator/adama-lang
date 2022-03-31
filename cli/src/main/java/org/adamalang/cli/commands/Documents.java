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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

public class Documents {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      documentsHelp(args);
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "connect":
        documentsConnect(config, next);
        return;
      case "create":
        documentsCreate(config, next);
        return;
      case "list":
        documentsList(config, next);
        return;
      case "attach":
        documentsAttach(config, next);
        return;
      case "help":
        documentsHelp(next);
        return;
    }
  }

  public static void documentsHelp(String[] args) {
    if (args.length > 0) {
      String command = Util.normalize(args[0]);
    }
    System.out.println(Util.prefix("Interact with documents in a limited fashion", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama documents", Util.ANSI.Green) + " " + Util.prefix("[SPACESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SPACESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("connect", Util.ANSI.Green) + "           Connect to a document");
    System.out.println("    " + Util.prefix("create", Util.ANSI.Green) + "            Create a document");
    System.out.println("    " + Util.prefix("list", Util.ANSI.Green) + "              List documents");
    System.out.println("    " + Util.prefix("attach", Util.ANSI.Green) + "            Attach an asset to a document");
  }

  private static void documentsConnect(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractOrCrash("--key", "-k", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/create");
        request.put("identity", identity);
        request.put("space", space);
        request.put("key", key);
        connection.stream(request, (response) -> {
          System.err.println(response.toPrettyString());
        });
      }
    }
  }

  private static void documentsCreate(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractOrCrash("--key", "-k", args);
    String arg = Util.extractOrCrash("--arg", "-aa", args);
    String entropy = Util.extractWithDefault("--entropy", "--e", null, args);
    ObjectNode argNode = Json.parseJsonObject(arg);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "document/create");
        request.put("identity", identity);
        request.put("space", space);
        request.put("key", key);
        if (entropy != null) {
          request.put("entropy", entropy);
        }
        request.set("arg", argNode);
        System.err.println(connection.execute(request).toPrettyString());
      }
    }
  }

  private static void documentsAttach(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractOrCrash("--key", "-k", args);
    String file = Util.extractOrCrash("--file", "-f", args);
    String filename = Util.extractWithDefault("--name", "-n", file, args);
    String contentTypeInfer = Files.probeContentType(new File(file).toPath());
    String contentType = Util.extractWithDefault("--type", "-t", contentTypeInfer, args);
    try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
      try (WebSocketClient client = new WebSocketClient(config)) {
        try (Connection connection = client.open()) {
          ObjectNode request = Json.newJsonObject();

          /*

      <method name="attachment/start" responder="progress" create="attachment-upload">
          <parameter name="identity"/>
          <parameter name="space"/>
          <parameter name="key"/>
          <parameter name="filename"/>
          <parameter name="content-type"/>
          <documentation>
          </documentation>
      </method>

      <method name="attachment/append" responder="simple" handler="attachment-upload" find-by="upload" error-find-by="477201">
          <parameter name="upload"/>
          <parameter name="chunk-md5"/>
          <parameter name="base64-bytes"/>
          <documentation>
          </documentation>
      </method>

      <method name="attachment/finish" responder="simple" handler="attachment-upload" find-by="upload" error-find-by="478227" destroy="true">
          <parameter name="upload"/>
          <documentation>
          </documentation>
      </method>
           */

          request.put("method", "attachment/start");
          request.put("identity", identity);
          request.put("space", space);
          request.put("key", key);
          request.put("filename", filename);
          request.put("content-type", contentType);
          System.err.println(request.toPrettyString());
          connection.stream(request, (update) -> {
            System.err.println(update.toPrettyString());
          });
        }
      }
    }
  }

  private static void documentsList(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String marker = Util.extractWithDefault("--marker", "-m", null, args);
    int limit = Integer.parseInt(Util.extractWithDefault("--limit", "-l", "1000", args));
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "document/list");
        request.put("identity", identity);
        request.put("space", space);
        if (marker != null) {
          request.put("marker", marker);
        }
        request.put("limit", limit);
        connection.stream(request, (item) -> {
          System.err.println(item.toPrettyString());
        });
      }
    }
  }
}
