package org.adamalang.common;

import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MachineIdentity {
    public final String ip;
    private final String trust;
    private final String cert;
    private final String key;

    public MachineIdentity(String json) throws Exception {
        ObjectNode tree = Json.parseJsonObject(json);
        this.ip = Json.readString(tree, "ip");
        this.key = Json.readString(tree, "key");
        this.cert = Json.readString(tree, "cert");
        this.trust = Json.readString(tree, "trust");
        if (ip == null) {
            throw new Exception("ip was not found in json object");
        }
        if (key == null) {
            throw new Exception("key was not found in json object");
        }
        if (cert == null) {
            throw new Exception("cert was not found in json object");
        }
        if (trust == null) {
            throw new Exception("trust was not found in json object");
        }
    }

    public ByteArrayInputStream getTrust() {
        return new ByteArrayInputStream(trust.getBytes(StandardCharsets.UTF_8));
    }

    public ByteArrayInputStream getKey() {
        return new ByteArrayInputStream(key.getBytes(StandardCharsets.UTF_8));
    }

    public ByteArrayInputStream getCert() {
        return new ByteArrayInputStream(cert.getBytes(StandardCharsets.UTF_8));
    }

    public static String convertToJson(String ip, File trust, File cert, File key) throws Exception {
        ObjectNode tree = Json.newJsonObject();
        tree.put("ip", ip);
        tree.put("trust", Files.readString(trust.toPath()));
        tree.put("cert", Files.readString(cert.toPath()));
        tree.put("key", Files.readString(key.toPath()));
        return tree.toString();
    }

    public static MachineIdentity fromFile(String file) throws Exception {
        return new MachineIdentity(Files.readString(new File(file).toPath()));
    }
}
