/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
import org.adamalang.transforms.results.Keystore;
import org.adamalang.validators.ValidateKeystore;

import java.io.File;
import java.nio.file.Files;
import java.security.PrivateKey;

public class AuthorityHandlerImpl implements AuthorityHandler {
    @Override
    public void create(Arguments.AuthorityCreateArgs args, Output.YesOrError output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "authority/create");
                request.put("identity", identity);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
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
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
    }
    private static File ensureFileDoesNotExist(String filename) throws Exception {
        File file = new File(filename);
        if (file.exists()) {
            throw new Exception(filename + " already exists, refusing to create");
        }
        return file;
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
            }
        }
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
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
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
                    System.err.println(item.toPrettyString());
                });
            }
        }
    }

    @Override
    public void createLocal(Arguments.AuthorityCreateLocalArgs args, Output.YesOrError output) throws Exception {
        //TODO
        System.out.println("TO BE IMPLEMENTED");
    }

    @Override
    public void appendLocal(Arguments.AuthorityAppendLocalArgs args, Output.YesOrError output) throws Exception {
        File newPrivateKeyFile = ensureFileDoesNotExist(args.priv);
        Keystore keystore = Keystore.parse(Files.readString(new File(args.keystore).toPath()));
        String privateKeyFile = keystore.generate(args.authority);
        Files.writeString(newPrivateKeyFile.toPath(), privateKeyFile);
        Files.writeString(new File(args.keystore).toPath(), keystore.persist());
    }

    @Override
    public void sign(Arguments.AuthoritySignArgs args, Output.YesOrError output) throws Exception {
        ObjectNode keyNode = Json.parseJsonObject(Files.readString(new File(args.key).toPath()));
        String authority = keyNode.get("authority").textValue();
        PrivateKey signingKey = Keystore.parsePrivateKey(keyNode);
        String token = Jwts.builder().setSubject(args.agent).setIssuer(authority).signWith(signingKey).compact();
        System.out.println(token);
        if (args.validate != null) {
            Keystore keystore = Keystore.parse(Files.readString(new File(args.validate).toPath()));
            NtPrincipal who = keystore.validate(authority, token);
            System.err.println("validated:" + who.agent);
        }
    }
}
