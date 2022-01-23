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
import io.jsonwebtoken.Jwts;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.transforms.results.Keystore;
import org.adamalang.validators.ValidateKeystore;

import java.io.File;
import java.nio.file.Files;
import java.security.PrivateKey;

public class Authority {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      authorityHelp();
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "create":
        authorityCreate(config, args);
        return;
      case "set":
        authoritySet(config, args);
        return;
      case "get":
        authorityGet(config, args);
        return;
      case "destroy":
        authorityDestroy(config, args);
        return;
      case "list":
        authorityList(config, args);
        return;
      case "create-local":
        authorityMakeKeyStore(config, args);
        return;
      case "append-local":
        authorityAppendKeyStore(config, args);
        return;
      case "sign":
        sign(config, args);
        return;
      case "help":
        authorityHelp();
        return;
    }
  }

  public static void sign(Config config, String[] args) throws Exception {
    String key = Util.extractOrCrash("--key", "-k", args);
    ObjectNode keyNode = Json.parseJsonObject(Files.readString(new File(key).toPath()));
    String authority = keyNode.get("authority").textValue();
    PrivateKey signingKey = Keystore.parsePrivateKey(keyNode);
    String agent = Util.extractOrCrash("--agent", "-ag", args);
    String token = Jwts.builder().setSubject(agent).setIssuer(authority).signWith(signingKey).compact();
    System.out.println(token);
    String validateAgainst = Util.extractWithDefault("--validate", "-v", null, args);
    if (validateAgainst != null) {
      Keystore keystore = Keystore.parse(Files.readString(new File(validateAgainst).toPath()));
      NtClient who = keystore.validate(authority, token);
      System.err.println("validated:" + who.agent);
    }
  }

  private static File ensureFileDoesNotExist(String filename) throws Exception {
    File file = new File(filename);
    if (file.exists()) {
      throw new Exception(filename + " already exists, refusing to create");
    }
    return file;
  }

  public static void authorityMakeKeyStore(Config config, String[] args) throws Exception {
    Util.extractOrCrash("--authority", "-a", args);
    String keystoreFile = Util.extractOrCrash("--keystore", "-k", args);
    String newKeyFile = Util.extractOrCrash("--private", "-p", args);
    ensureFileDoesNotExist(newKeyFile);
    Files.writeString(ensureFileDoesNotExist(keystoreFile).toPath(), "{}");
    authorityAppendKeyStore(config, args);
  }

  public static void authorityAppendKeyStore(Config config, String[] args) throws Exception {
    String authority = Util.extractOrCrash("--authority", "-a", args);
    String keystoreFile = Util.extractOrCrash("--keystore", "-k", args);
    String newKeyFile = Util.extractOrCrash("--private", "-p", args);
    File newPrivateKeyFile = ensureFileDoesNotExist(newKeyFile);
    Keystore keystore = Keystore.parse(Files.readString(new File(keystoreFile).toPath()));
    String privateKeyFile = keystore.generate(authority);
    Files.writeString(newPrivateKeyFile.toPath(), privateKeyFile);
    Files.writeString(new File(keystoreFile).toPath(), keystore.persist());
  }

  public static void authorityCreate(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/create");
        request.put("identity", identity);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  public static void authoritySet(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String authority = Util.extractOrCrash("--authority", "-a", args);
    String keystoreFile = Util.extractOrCrash("--keystore", "-k", args);
    String keystoreJson = Files.readString(new File(keystoreFile).toPath());
    ValidateKeystore.validate(Json.parseJsonObject(keystoreJson));
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/set");
        request.put("identity", identity);
        request.put("authority", authority);
        request.set("key-store", Json.parseJsonObject(keystoreJson));
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  public static void authorityGet(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String authority = Util.extractOrCrash("--authority", "-a", args);
    String keystoreFile = Util.extractOrCrash("--keystore", "-k", args);
    ensureFileDoesNotExist(keystoreFile);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/get");
        request.put("identity", identity);
        request.put("authority", authority);
        ObjectNode response = connection.execute(request);
        Files.writeString(new File(keystoreFile).toPath(), response.get("keystore").toString());
      }
    }
  }

  public static void authorityDestroy(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String authority = Util.extractOrCrash("--authority", "-a", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/destroy");
        request.put("identity", identity);
        request.put("authority", authority);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  public static void authorityList(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/list");
        request.put("identity", identity);
        connection.stream(request, (item) -> {
          System.err.println(item.toPrettyString());
        });
      }
    }
  }

  public static void authorityHelp() {
    System.out.println(Util.prefix("Manage how you and your users can interact with the Adama platform.", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama authority", Util.ANSI.Green) + " " + Util.prefix("[AUTHORITYSUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("AUTHORITYSUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("create", Util.ANSI.Green) + "            Creates a new authority");
    System.out.println("    " + Util.prefix("destroy", Util.ANSI.Green) + "           Destroy an authority " + Util.prefix("(WARNING)", Util.ANSI.Red));
    System.out.println("    " + Util.prefix("list", Util.ANSI.Green) + "              List authorities this developer owns");
    System.out.println("    " + Util.prefix("set", Util.ANSI.Green) + "               Set public keys to an authority");
    System.out.println("    " + Util.prefix("get", Util.ANSI.Green) + "               Get released public keys for an authority");
    System.out.println("    " + Util.prefix("create-local", Util.ANSI.Green) + "      Make a new set of public keys" + Util.prefix("(WARNING)", Util.ANSI.Red));
    System.out.println("    " + Util.prefix("append-local", Util.ANSI.Green) + "      Append a new public key to the public key file" + Util.prefix("(WARNING)", Util.ANSI.Red));
    System.out.println("    " + Util.prefix("sign", Util.ANSI.Green) + "              Sign an agent with a local private key");
  }
}
