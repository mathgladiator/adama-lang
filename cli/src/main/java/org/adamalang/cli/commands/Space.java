/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Hashing;
import org.adamalang.common.Json;
import org.adamalang.common.Validators;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.runtime.deploy.DeploymentFactory;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.DeploymentPlan;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.web.assets.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;
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
      case "set-rxhtml":
        spaceSetRxHTML(config, next);
        return;
      case "get-rxhtml":
        spaceGetRxHTML(config, next);
        return;
      case "upload":
        spaceUpload(config, next);
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
      case "generate-key":
        spaceGenerateKey(config, next);
        return;
      case "encrypt-secret":
        spaceEncryptSecret(config, next);
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
    System.out.println("    " + Util.prefix("generate-key", Util.ANSI.Green) + "      Generate a server-side key to use for storing secrets");
    System.out.println("    " + Util.prefix("encrypt-secret", Util.ANSI.Green) + "    Encrypt a secret to store within code");
    System.out.println("    " + Util.prefix("set-rxhtml", Util.ANSI.Green) + "        Set the frontend RxHTML forest");
    System.out.println("    " + Util.prefix("get-rxhtml", Util.ANSI.Green) + "        Get the frontend RxHTML forest");
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
      ObjectNode version = plan.putObject("versions").putObject("file");
      version.put("main", singleScript);
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

  private static void spaceSetRxHTML(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String singleFile = Util.extractWithDefault("--file", "-f", null, args);
    String source = Files.readString(new File(singleFile).toPath());
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/set-rxhtml");
        request.put("identity", identity);
        request.put("space", space);
        request.put("rxhtml", source);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  private static void spaceGetRxHTML(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/get-rxhtml");
        request.put("identity", identity);
        request.put("space", space);
        ObjectNode response = connection.execute(request);
        System.err.println(response.toPrettyString());
      }
    }
  }

  private static void addFileToLocalBundle(File file, String prefix, HashMap<String, NtAsset> found) throws Exception {
    String name = prefix + file.getName();
    String contentType = ContentType.of(file.getName());
    FileInputStream input = new FileInputStream(file);
    try {
      byte[] buffer = new byte[8196];
      int rd;
      long size = 0;
      MessageDigest md5 = Hashing.md5();
      MessageDigest sha384 = Hashing.sha384();
      while ((rd = input.read(buffer)) >= 0) {
        size += rd;
        md5.update(buffer, 0, rd);
        sha384.update(buffer, 0, rd);
      }
      found.put(name, new NtAsset("?", name, contentType, size, Hashing.finishAndEncode(md5), Hashing.finishAndEncode(sha384)));
    } finally {
      input.close();
    }
  }

  private static void fillLocalBundle(File root, String prefix, HashMap<String, NtAsset> found) throws Exception {
    for (File file : root.listFiles()) {
      if (file.isDirectory()) {
        fillLocalBundle(file, prefix + file.getName() + "/", found);
      } else {
        addFileToLocalBundle(file, prefix, found);
      }
    }
  }

  private static HashMap<String, NtAsset> buildRemoteListing(ObjectNode ideDoc) {
    HashMap<String, NtAsset> remote = new HashMap<>();
    try {
      JsonNode assetsNode = ideDoc.get("delta").get("data").get("assets");
      if (assetsNode != null && assetsNode.isObject()) {
        Iterator<Map.Entry<String, JsonNode>> fields = assetsNode.fields();
        while (fields.hasNext()) {
          Map.Entry<String, JsonNode> field = fields.next();
          try {
            int id = Integer.parseInt(field.getKey());
            ObjectNode asset = (ObjectNode) field.getValue();
            remote.put(asset.get("name").textValue(), new NtAsset("" + id, asset.get("name").textValue(), asset.get("type").textValue(), asset.get("size").longValue(), asset.get("md5").textValue(), asset.get("sha384").textValue()));
          } catch (NumberFormatException nfe) {
            // not a object
          }
        }
      }
    } catch (NullPointerException npe) {

    }
    return remote;
  }

  private static void spaceUpload(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String doGCRaw = Util.extractWithDefault("--gc", "-g", "no", args);
    boolean gc = "yes".equalsIgnoreCase(doGCRaw) || "true".equalsIgnoreCase(doGCRaw);

    HashMap<String, NtAsset> bundle = new HashMap<>();

    String root = Util.extractWithDefault("--root", "-r", null, args);
    String singleFilename = null;
    if (root != null) {
      File rootPath = new File(root);
      if (!(rootPath.exists() && rootPath.isDirectory())) {
        throw new Exception("--root is not a directory");
      }
      fillLocalBundle(rootPath, "", bundle);
    } else {
      singleFilename = Util.extractOrCrash("--file", "-f",  args);
      if (singleFilename != null) {
        addFileToLocalBundle(new File(singleFilename), "", bundle);
      }
    }

    // TODO: scan for special things (redirects...)

    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode start = Json.newJsonObject();
        start.put("method", "connection/create");
        start.put("identity", identity);
        start.put("space", "ide");
        start.put("key", space);

        BlockingDeque<Connection.IdObject> ideQueue = connection.stream_queue(start);
        Connection.IdObject first = ideQueue.take();
        ObjectNode ideDoc = first.node();
        try {
          HashMap<String, NtAsset> remote = buildRemoteListing(ideDoc);

          HashMap<String, NtAsset> todo = new HashMap<>();
          Iterator<Map.Entry<String, NtAsset>> it = bundle.entrySet().iterator();
          while (it.hasNext()) {
            Map.Entry<String, NtAsset> current = it.next();
            NtAsset prior = remote.get(current.getKey());
            if (prior != null && prior.md5.equals(current.getValue().md5) && prior.sha384.equals(current.getValue().sha384)) {
              System.out.println("skipping:" + current.getKey());
            } else {
              todo.put(current.getKey(), current.getValue());
            }
          }

          for (Map.Entry<String, NtAsset> entry : todo.entrySet()) {
            ObjectNode request = Json.newJsonObject();
            request.put("method", "attachment/start");
            request.put("identity", identity);
            request.put("space", "ide");
            request.put("key", space);
            request.put("filename", entry.getValue().name);
            request.put("content-type", entry.getValue().contentType);

            FileInputStream fileInput = root != null ? new FileInputStream(new File(new File(root), entry.getKey())) : new FileInputStream(new File(entry.getKey()));
            try {
              System.out.println("upload:" + entry.getKey());
              BlockingDeque<Connection.IdObject> queue = connection.stream_queue(request);
              boolean finished = false;
              while (!finished) {
                Connection.IdObject obj = queue.take();
                int chunkSize = obj.node().get("chunk_request_size").intValue();
                int uploadId = obj.id;
                byte[] chunk = new byte[chunkSize];
                int len = 0;
                int rd;
                MessageDigest chunkDigest = Hashing.md5();
                while (len < chunkSize && (rd = fileInput.read(chunk, len, chunkSize - len)) >= 0) {
                  chunkDigest.update(chunk, len, rd);
                  len += rd;
                }
                finished = len < chunkSize;
                if (len > 0) {
                  byte[] range = len == chunk.length ? chunk : Arrays.copyOfRange(chunk, 0, len);
                  ObjectNode append = Json.newJsonObject();
                  append.put("method", "attachment/append");
                  append.put("upload", uploadId);
                  append.put("chunk-md5", Hashing.finishAndEncode(chunkDigest));
                  append.put("base64-bytes", new String(Base64.getEncoder().encode(range)));
                  connection.execute(append);
                }

                if (finished) {
                  ObjectNode finish = Json.newJsonObject();
                  finish.put("method", "attachment/finish");
                  finish.put("upload", uploadId);
                  connection.execute(finish);
                }
              }
            } finally {
              fileInput.close();
            }
          }

          if (gc) {
            for (Map.Entry<String, NtAsset> prior : remote.entrySet()) {
              if (!bundle.containsKey(prior.getKey())) {
                System.err.println("delete:" + prior.getKey());
                ObjectNode delete_resource = Json.newJsonObject();
                delete_resource.put("method", "connection/send");
                delete_resource.put("connection", first.id);
                delete_resource.put("channel", "delete_resource");
                delete_resource.putObject("message").put("id", prior.getValue().id);
                connection.execute(delete_resource);
              }
            }
          }
        } finally {
          ObjectNode finished = Json.newJsonObject();
          finished.put("method", "connection/end");
          finished.put("connection", first.id);
          connection.execute(finished);
        }
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
    String writeAs = Util.extractOrCrash("--output", "-o", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/reflect");
        request.put("identity", identity);
        request.put("space", space);
        request.put("key", key);
        ObjectNode response = connection.execute(request);
        Files.writeString(new File(writeAs).toPath(), response.toPrettyString());
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

  private static void spaceGenerateKey(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/generate-key");
        request.put("identity", identity);
        request.put("space", space);
        ObjectNode response = connection.execute(request);
        config.manipulate((node) -> {
          ObjectNode keys = null;
          if (node.has("space-keys")) {
            keys = (ObjectNode) node.get("space-keys");
          } else {
            keys = node.putObject("space-keys");
          }
          keys.set(space, response);
        });
        System.err.println("Server Key Created");
      }
    }
  }

  private static void spaceEncryptSecret(Config config, String[] args) throws Exception {
    ObjectNode keys = config.get_or_create_child("space-keys");
    String space = Util.extractOrCrash("--space", "-s", args);
    if (!keys.has(space)) {
      System.err.println("Config doesn't have space-keys configured; we lack the public key for the space!");
      return;
    }
    ObjectNode response = (ObjectNode) keys.get(space);
    String publicKey = response.get("publicKey").textValue();
    int keyId = response.get("keyId").intValue();

    KeyPair ephemeral = PublicPrivateKeyPartnership.genKeyPair();
    byte[] sharedSecret = PublicPrivateKeyPartnership.secretFrom( //
        PublicPrivateKeyPartnership.keyPairFrom(publicKey, //
            PublicPrivateKeyPartnership.privateKeyOf(ephemeral))); //

    System.out.print(Util.prefix("Secret/Token:", Util.ANSI.Red));
    String secret = new String(System.console().readPassword());
    String cipher = PublicPrivateKeyPartnership.encrypt(sharedSecret, secret);
    String encrypted = keyId + ";" + PublicPrivateKeyPartnership.publicKeyOf(ephemeral) + ";" + cipher;
    System.out.println("Encrypted Secret:");
    System.out.println("------------------");
    System.out.println(encrypted);
    System.out.println("------------------");
  }
}
