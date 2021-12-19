package org.adamalang.grpc.common;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class MachineIdentity {
    private final String trust;
    private final String cert;
    private final String key;

    public MachineIdentity(String json) throws Exception {
        String _trust = null;
        String _cert = null;
        String _key = null;

        JsonStreamReader reader = new JsonStreamReader(json);
        if (reader.startObject()) {
            while (reader.notEndOfObject()) {
                switch(reader.fieldName()) {
                    case "trust":
                        _trust = reader.readString();
                        break;
                    case "cert":
                        _cert = reader.readString();
                        break;
                    case "key":
                        _key = reader.readString();
                        break;
                }
            }
        }
        if (_key == null) {
            throw new Exception("key was not found in json object");
        }
        if (_cert == null) {
            throw new Exception("cert was not found in json object");
        }
        if (_trust == null) {
            throw new Exception("trust was not found in json object");
        }
        key = _key;
        cert = _cert;
        trust = _trust;
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

    public static String convertToJson(File trust, File cert, File key) throws Exception {
        JsonStreamWriter writer = new JsonStreamWriter();
        writer.beginObject();
        writer.writeObjectFieldIntro("trust");
        writer.writeString(Files.readString(trust.toPath()));
        writer.writeObjectFieldIntro("cert");
        writer.writeString(Files.readString(cert.toPath()));
        writer.writeObjectFieldIntro("key");
        writer.writeString(Files.readString(key.toPath()));
        writer.endObject();
        return writer.toString();
    }

    public static MachineIdentity fromFile(String file) throws Exception {
        return new MachineIdentity(Files.readString(new File(file).toPath()));
    }
}
