package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.ArgumentType;
import org.adamalang.cli.router.AuthorityHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.transforms.results.Keystore;
import org.adamalang.validators.ValidateKeystore;

import java.io.File;
import java.nio.file.Files;
import java.security.PrivateKey;

public class AuthorityHandlerImpl implements AuthorityHandler{


    @Override
    public int createAuthority(ArgumentType.CreateAuthorityArgs args, Output output) throws Exception {
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
        return 0;
    }

    @Override
    public int setAuthority(ArgumentType.SetAuthorityArgs args, Output output) throws Exception {
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
        return 0;
    }

    @Override
    public int getAuthority(ArgumentType.GetAuthorityArgs args, Output output) throws Exception {
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
        return 0;
    }

    @Override
    public int destroyAuthority(ArgumentType.DestroyAuthorityArgs args, Output output) throws Exception {
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
        return 0;
    }

    @Override
    public int listAuthority(ArgumentType.ListAuthorityArgs args, Output output) throws Exception {
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
        return 0;
    }

    @Override
    public int createLocalAuthority(ArgumentType.CreateLocalAuthorityArgs args, Output output) throws Exception {
        ensureFileDoesNotExist(args.priv);
        Files.writeString(ensureFileDoesNotExist(args.keystore).toPath(), "{}");
        File newPrivateKeyFile = ensureFileDoesNotExist(args.priv);
        Keystore keystore = Keystore.parse(Files.readString(new File(args.keystore).toPath()));
        String privateKeyFile = keystore.generate(args.authority);
        Files.writeString(newPrivateKeyFile.toPath(), privateKeyFile);
        Files.writeString(new File(args.keystore).toPath(), keystore.persist());
        return 0;

    }

    @Override
    public int appendLocalAuthority(ArgumentType.AppendLocalAuthorityArgs args, Output output) throws Exception {
        File newPrivateKeyFile = ensureFileDoesNotExist(args.priv);
        Keystore keystore = Keystore.parse(Files.readString(new File(args.keystore).toPath()));
        String privateKeyFile = keystore.generate(args.authority);
        Files.writeString(newPrivateKeyFile.toPath(), privateKeyFile);
        Files.writeString(new File(args.keystore).toPath(), keystore.persist());
        return 0;
    }

    @Override
    public int signAuthority(ArgumentType.SignAuthorityArgs args, Output output) throws Exception {
        String key = args.key;
        ObjectNode keyNode = Json.parseJsonObject(Files.readString(new File(key).toPath()));
        String authority = keyNode.get("authority").textValue();
        PrivateKey signingKey = Keystore.parsePrivateKey(keyNode);
        String agent = args.agent;
        String token = Jwts.builder().setSubject(agent).setIssuer(authority).signWith(signingKey).compact();
        System.out.println(token);
        String validateAgainst = args.validate;
        if (validateAgainst != null) {
            Keystore keystore = Keystore.parse(Files.readString(new File(validateAgainst).toPath()));
            NtPrincipal who = keystore.validate(authority, token);
            System.err.println("validated:" + who.agent);
        }
        return 0;
    }

    private static File ensureFileDoesNotExist(String filename) throws Exception {
        File file = new File(filename);
        if (file.exists()) {
            throw new Exception(filename + " already exists, refusing to create");
        }
        return file;
    }
}