/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.CoreServicesNexus;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.implementations.space.Deployer;
import org.adamalang.cli.implementations.space.Uploader;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.SpaceHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.common.Validators;
import org.adamalang.common.keys.PublicPrivateKeyPartnership;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.CoreServices;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.function.Supplier;

public class SpaceHandlerImpl implements SpaceHandler {

  @Override
  public void create(Arguments.SpaceCreateArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    if (!Validators.simple(args.space, 127)) {
      System.err.println("Space name `" + args.space + "` is not valid");
      return;
    }
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/create");
        request.put("identity", identity);
        request.put("space", args.space);
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void metrics(Arguments.SpaceMetricsArgs args, Output.JsonOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/metrics");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("prefix", args.prefix == null ? "" : args.prefix);
        output.add(connection.execute(request));
        output.out();
      }
    }
  }

  @Override
  public void delete(Arguments.SpaceDeleteArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/delete");
        request.put("identity", identity);
        request.put("space", args.space);
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void deploy(Arguments.SpaceDeployArgs args, Output.YesOrError output) throws Exception {
    CoreServices.install(CoreServicesNexus.NOOP());
    Deployer.deploy(args, output);
  }

  @Override
  public void developers(Arguments.SpaceDevelopersArgs args, Output.JsonOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/list-developers");
        request.put("identity", identity);
        request.put("space", args.space);
        connection.stream(request, (cId, response) -> {
          output.add(response);
        });
      }
    }
    output.out();
  }

  @Override
  public void encryptPriv(Arguments.SpaceEncryptPrivArgs args, Output.YesOrError output) throws Exception {
    encrypt(args.config, args.space, () -> {
      try {
        return Files.readString(new File(args.priv).toPath());
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }, output);
  }

  @Override
  public void encryptSecret(Arguments.SpaceEncryptSecretArgs args, Output.YesOrError output) throws Exception {
    encrypt(args.config, args.space, () -> {
      System.out.print(Util.prefix("Secret/Token:", Util.ANSI.Red));
      return new String(System.console().readPassword());
    }, output);
  }

  private static void encrypt(Config config, String space, Supplier<String> secretSupplier, Output.YesOrError output) throws Exception {
    ObjectNode keys = config.get_or_create_child("space-keys");
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

    String secret = secretSupplier.get();
    String cipher = PublicPrivateKeyPartnership.encrypt(sharedSecret, secret);
    String encrypted = keyId + ";" + PublicPrivateKeyPartnership.publicKeyOf(ephemeral) + ";" + cipher;
    System.out.println("Encrypted Secret:");
    System.out.println("------------------");
    System.out.println(encrypted);
    System.out.println("------------------");
    output.out();
  }

  @Override
  public void generateKey(Arguments.SpaceGenerateKeyArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/generate-key");
        request.put("identity", identity);
        request.put("space", args.space);
        ObjectNode response = connection.execute(request);
        config.manipulate((node) -> {
          ObjectNode keys = null;
          if (node.has("space-keys")) {
            keys = (ObjectNode) node.get("space-keys");
          } else {
            keys = node.putObject("space-keys");
          }
          keys.set(args.space, response);
        });
        output.out();
      }
    }
  }

  @Override
  public void get(Arguments.SpaceGetArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/get");
        request.put("identity", identity);
        request.put("space", args.space);
        ObjectNode response = connection.execute(request);
        Files.writeString(new File(args.output).toPath(), response.get("plan").toPrettyString());
        output.out();
      }
    }
  }

  @Override
  public void getRxhtml(Arguments.SpaceGetRxhtmlArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/get-rxhtml");
        request.put("identity", identity);
        request.put("space", args.space);
        ObjectNode response = connection.execute(request);
        Files.writeString(new File(args.output).toPath(), response.get("rxhtml").textValue());
        output.out();
      }
    }
  }

  @Override
  public void list(Arguments.SpaceListArgs args, Output.JsonOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    int limit = Integer.parseInt(args.limit);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/list");
        request.put("identity", identity);
        if (!"".equals(args.marker)) {
          request.put("marker", args.marker);
        }
        request.put("limit", limit);
        connection.stream(request, (cId, response) -> {
          output.add(response);
        });
      }
    }
    output.out();
  }

  @Override
  public void reflect(Arguments.SpaceReflectArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/reflect");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("key", args.key == null ? "default" : args.key);
        ObjectNode response = connection.execute(request);
        Files.writeString(new File(args.output).toPath(), response.get("reflection").toPrettyString());
        output.out();
      }
    }
  }

  @Override
  public void setRole(Arguments.SpaceSetRoleArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/set-role");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("email", args.email);
        request.put("role", args.role);
        connection.execute(request);
        output.out();
      }
    }

  }

  @Override
  public void setRxhtml(Arguments.SpaceSetRxhtmlArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    String source = Files.readString(new File(args.file).toPath());
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/set-rxhtml");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("rxhtml", source);
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void setPolicy(Arguments.SpaceSetPolicyArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    String source = Files.readString(new File(args.file).toPath());
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/set-policy");
        request.put("identity", identity);
        request.put("space", args.space);
        request.set("access-policy", Json.parseJsonObject(source));
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void getPolicy(Arguments.SpaceGetPolicyArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/get-policy");
        request.put("identity", identity);
        request.put("space", args.space);
        Files.writeString(new File(args.output).toPath(), connection.execute(request).get("policy").toPrettyString());
        output.out();
      }
    }
  }

  @Override
  public void upload(Arguments.SpaceUploadArgs args, Output.JsonOrError output) throws Exception {
    Uploader.upload(args, output);
  }
}
