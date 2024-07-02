/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
import io.jsonwebtoken.Jwts;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.AuthorityHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.security.Keystore;
import org.adamalang.validators.ValidateKeystore;

import java.io.File;
import java.nio.file.Files;
import java.security.PrivateKey;

public class AuthorityHandlerImpl implements AuthorityHandler {
  @Override
  public void appendLocal(Arguments.AuthorityAppendLocalArgs args, Output.YesOrError output) throws Exception {
    File newPrivateKeyFile = ensureFileDoesNotExist(args.priv);
    File existingKeystoreFile = new File(args.keystore);
    append(args.authority, existingKeystoreFile, newPrivateKeyFile);
    output.out();
  }

  @Override
  public void create(Arguments.AuthorityCreateArgs args, Output.JsonOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/create");
        request.put("identity", identity);
        output.add(connection.execute(request));
        output.out();
      }
    }
  }

  @Override
  public void createLocal(Arguments.AuthorityCreateLocalArgs args, Output.YesOrError output) throws Exception {
    File newPrivateKeyFile = ensureFileDoesNotExist(args.priv);
    File newKeystoreFile = ensureFileDoesNotExist(args.keystore);
    Files.writeString(newKeystoreFile.toPath(), "{}");
    append(args.authority, newKeystoreFile, newPrivateKeyFile);
    output.out();
  }

  @Override
  public void destroy(Arguments.AuthorityDestroyArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/destroy");
        request.put("identity", identity);
        request.put("authority", args.authority);
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void get(Arguments.AuthorityGetArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    ensureFileDoesNotExist(args.keystore);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/get");
        request.put("identity", identity);
        request.put("authority", args.authority);
        ObjectNode response = connection.execute(request);
        Files.writeString(new File(args.keystore).toPath(), response.get("keystore").toString());
        output.out();
      }
    }
  }

  @Override
  public void list(Arguments.AuthorityListArgs args, Output.JsonOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/list");
        request.put("identity", identity);
        connection.stream(request, (cId, item) -> {
          output.add(item);
        });
        output.out();
      }
    }
  }

  @Override
  public void set(Arguments.AuthoritySetArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    String keystoreJson = Files.readString(new File(args.keystore).toPath());
    ValidateKeystore.validate(Json.parseJsonObject(keystoreJson));
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "authority/set");
        request.put("identity", identity);
        request.put("authority", args.authority);
        request.set("key-store", Json.parseJsonObject(keystoreJson));
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void sign(Arguments.AuthoritySignArgs args, Output.JsonOrError output) throws Exception {
    ObjectNode keyNode = Json.parseJsonObject(Files.readString(new File(args.key).toPath()));
    String authority = keyNode.get("authority").textValue();
    PrivateKey signingKey = Keystore.parsePrivateKey(keyNode);
    String token = Jwts.builder().subject(args.agent).issuer(authority).signWith(signingKey).compact();
    ObjectNode tokenBundle = Json.newJsonObject();
    tokenBundle.put("token", token);
    output.add(tokenBundle);
    if (args.validate != null) {
      Keystore keystore = Keystore.parse(Files.readString(new File(args.validate).toPath()));
      NtPrincipal who = keystore.validate(authority, token);
      tokenBundle.put("validated", true);
    }
    output.out();
  }

  private static File ensureFileDoesNotExist(String filename) throws Exception {
    File file = new File(filename);
    if (file.exists()) {
      throw new Exception(filename + " already exists, refusing to create");
    }
    return file;
  }

  private void append(String authority, File keystoreFile, File keyFile) throws Exception {
    Keystore keystore = Keystore.parse(Files.readString(keystoreFile.toPath()));
    String privateKeyFile = keystore.generate(authority);
    Files.writeString(keyFile.toPath(), privateKeyFile);
    Files.writeString(keystoreFile.toPath(), keystore.persist());
  }
}
