/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Hashing;
import org.adamalang.common.Json;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

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
      case "delete":
        documentsDelete(config, next);
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
    System.out.println("    " + Util.prefix("delete", Util.ANSI.Green) + "            Delete a document");
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
        connection.stream(request, (cId, response) -> {
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

  private static void documentsDelete(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractOrCrash("--key", "-k", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "document/delete");
        request.put("identity", identity);
        request.put("space", space);
        request.put("key", key);
        System.err.println(connection.execute(request).toPrettyString());
      }
    }
  }

  private static void documentsAttach(Config config, String[] args) throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("offload");
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
          request.put("method", "attachment/start");
          request.put("identity", identity);
          request.put("space", space);
          request.put("key", key);
          request.put("filename", filename);
          request.put("content-type", contentType);
          connection.stream(request, (cId, ask) -> {
            executor.execute(new NamedRunnable("name") {
              @Override
              public void execute() throws Exception {
                int sz = ask.get("chunk_request_size").intValue();
                byte[] chunk = new byte[sz];
                try {
                  int rd = input.read(chunk);
                  if (rd > 0) {
                    System.err.println("Uploading..." + rd + " bytes");
                    MessageDigest digest = Hashing.md5();
                    digest.update(chunk, 0, rd);
                    ObjectNode append = Json.newJsonObject();
                    append.put("method", "attachment/append");
                    append.put("upload", cId.intValue());
                    append.put("chunk-md5", Hashing.finishAndEncode(digest));
                    append.put("base64-bytes", Base64.getEncoder().encodeToString(Arrays.copyOfRange(chunk, 0, rd)));
                    connection.execute(append);
                  } else {
                    System.err.println("Finishing");
                    ObjectNode append = Json.newJsonObject();
                    append.put("method", "attachment/finish");
                    append.put("upload", cId.intValue());
                    connection.execute(append);
                    connection.close();
                    executor.shutdown();
                  }
                } catch (Exception ioe) {
                  executor.shutdown();
                }
              }
            });
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
        connection.stream(request, (cId, item) -> {
          System.err.println(item.toPrettyString());
        });
      }
    }
  }
}
