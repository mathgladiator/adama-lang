package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.commands.Code;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.ArgumentType;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.SpaceHandler;
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
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class SpaceHandlerImpl implements SpaceHandler {

    @Override
    public int createSpace(ArgumentType.CreateSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        if (!Validators.simple(args.space, 127)) {
            System.err.println("Space name `" + args.space + "` is not valid");
            return 1;
        }
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/create");
                request.put("identity", identity);
                request.put("space", args.space);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
        return 0;
    }

    @Override
    public int deleteSpace(ArgumentType.DeleteSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/delete");
                request.put("identity", identity);
                request.put("space", args.space);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
        return 0;
    }

    @Override
    public int deploySpace(ArgumentType.DeploySpaceArgs args, Output output) throws Exception {
        return 0;
    }

    @Override
    public int setRxhtmlSpace(ArgumentType.SetRxhtmlSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        String source = Files.readString(new File(args.file).toPath());
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/set-rxhtml");
                request.put("identity", identity);
                request.put("space", args.space);
                request.put("rxhtml", source);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
        return 0;
    }

    @Override
    public int getRxhtmlSpace(ArgumentType.GetRxhtmlSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/get-rxhtml");
                request.put("identity", identity);
                request.put("space", args.space);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
        return 0;
    }
    @Override
    public int uploadSpace(ArgumentType.UploadSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        String space = args.space;
        String doGCRaw = args.gc;
        boolean gc = "yes".equalsIgnoreCase(doGCRaw) || "true".equalsIgnoreCase(doGCRaw);

        HashMap<String, NtAsset> bundle = new HashMap<>();

        String root = args.root;
        String singleFilename = null;
        if (root != null) {
            File rootPath = new File(root);
            if (!(rootPath.exists() && rootPath.isDirectory())) {
                throw new Exception("--root is not a directory");
            }
            fillLocalBundle(rootPath, "", bundle);
        } else {
            singleFilename = args.file;
            if (singleFilename != null) {
                addFileToLocalBundle(new File(singleFilename), "", bundle);
            }
        }

        // TODO: scan for special things (redirects...)

        try (WebSocketClient client = new WebSocketClient(args.config)) {
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
        return 0;
    }

    @Override
    public int downloadSpace(ArgumentType.DownloadSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/get");
                request.put("identity", identity);
                request.put("space", args.space);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
        return 0;
    }

    @Override
    public int listSpace(ArgumentType.ListSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);

        String marker = args.marker;
        int limit = Integer.parseInt(args.limit);

        try (WebSocketClient client = new WebSocketClient(args.config)) {
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
        return 0;
    }

    @Override
    public int usageSpace(ArgumentType.UsageSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        int limit = Integer.parseInt(args.limit);

        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/usage");
                request.put("identity", identity);
                request.put("space", args.space);
                request.put("limit", limit);
                connection.stream(request, (cId, response) -> {
                    System.err.println(response.toPrettyString());
                });
            }
        }
        return 0;
    }

    @Override
    public int reflectSpace(ArgumentType.ReflectSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/reflect");
                request.put("identity", identity);
                request.put("space", args.space);
                request.put("key", args.key);
                ObjectNode response = connection.execute(request);
                Files.writeString(new File(args.output).toPath(), response.toPrettyString());
            }
        }
        return 0;
    }

    @Override
    public int setRoleSpace(ArgumentType.SetRoleSpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/set-role");
                request.put("identity", identity);
                request.put("space", args.space);
                request.put("email", args.email);
                request.put("role", args.role);
                ObjectNode response = connection.execute(request);
                System.err.println(response.toPrettyString());
            }
        }
        return 0;
    }

    @Override
    public int generateKeySpace(ArgumentType.GenerateKeySpaceArgs args, Output output) throws Exception {
        String identity = args.config.get_string("identity", null);
        try (WebSocketClient client = new WebSocketClient(args.config)) {
            try (Connection connection = client.open()) {
                ObjectNode request = Json.newJsonObject();
                request.put("method", "space/generate-key");
                request.put("identity", identity);
                request.put("space", args.space);
                ObjectNode response = connection.execute(request);
                args.config.manipulate((node) -> {
                    ObjectNode keys = null;
                    if (node.has("space-keys")) {
                        keys = (ObjectNode) node.get("space-keys");
                    } else {
                        keys = node.putObject("space-keys");
                    }
                    keys.set(args.space, response);
                });
                System.err.println("Server Key Created");
            }
        }
        return 0;
    }

    @Override
    public int encryptSecretSpace(ArgumentType.EncryptSecretSpaceArgs args, Output output) throws Exception {
        ObjectNode keys = args.config.get_or_create_child("space-keys");
        if (!keys.has(args.space)) {
            System.err.println("Config doesn't have space-keys configured; we lack the public key for the space!");
            return 1;
        }
        ObjectNode response = (ObjectNode) keys.get(args.space);
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
        return 0;
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


}
