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
package org.adamalang.cli.implementations.space;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Hashing;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.assets.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.function.Function;

public class Uploader {

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

  private static boolean hasRemoteListing(ObjectNode ideDoc) {
    try {
      return ideDoc.get("delta").has("data");
    } catch (Exception ex) {
      return false;
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

  public static void upload(Arguments.SpaceUploadArgs args, Output.JsonOrError output) throws Exception {
    Config config = args.config;
    String identity = config.get_string("identity", null);
    String space = args.space;
    boolean gc = "yes".equalsIgnoreCase(args.gc) || "true".equalsIgnoreCase(args.gc);
    HashMap<String, NtAsset> bundle = new HashMap<>();
    final Function<String, File> fetch;
    if (args.directory != null) {
      File rootPath = new File(args.directory);
      if (!(rootPath.exists() && rootPath.isDirectory())) {
        throw new Exception("--directory is not a directory");
      }
      fillLocalBundle(rootPath, "", bundle);
      fetch = (path) -> new File(rootPath, path);
    } else if (args.file != null) {
      File singleFile = new File(args.file);
      if (!singleFile.exists()) {
        throw new Exception("--file `" + args.file + "` must exist");
      }
      String prefix = singleFile.getParent();
      if (prefix == null) {
        prefix = "";
      } else {
        prefix += "/";
      }
      addFileToLocalBundle(singleFile, prefix, bundle);
      fetch = (path) -> new File(path);
    } else {
      throw new Exception("requires either --directory or --file");
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
        if (!hasRemoteListing(ideDoc)) { // skip the view filter update
          first = ideQueue.take();
          ideDoc = first.node();
        }
        try {
          HashMap<String, NtAsset> remote = buildRemoteListing(ideDoc);

          HashMap<String, NtAsset> todo = new HashMap<>();
          Iterator<Map.Entry<String, NtAsset>> it = bundle.entrySet().iterator();
          while (it.hasNext()) {
            Map.Entry<String, NtAsset> current = it.next();
            NtAsset prior = remote.get(current.getKey());
            if (prior != null && prior.md5.equals(current.getValue().md5) && prior.sha384.equals(current.getValue().sha384)) {
              ObjectNode skipResult = Json.newJsonObject();
              skipResult.put("result", "skipped");
              skipResult.put("file", current.getKey());
              output.add(skipResult);
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

            FileInputStream fileInput = new FileInputStream(fetch.apply(entry.getKey()));
            try {
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
            ObjectNode addResult = Json.newJsonObject();
            addResult.put("result", "uploaded");
            addResult.put("file", entry.getKey());
            output.add(addResult);
          }

          if (gc) {
            for (Map.Entry<String, NtAsset> prior : remote.entrySet()) {
              if (!bundle.containsKey(prior.getKey())) {
                ObjectNode delete_resource = Json.newJsonObject();
                delete_resource.put("method", "connection/send");
                delete_resource.put("connection", first.id);
                delete_resource.put("channel", "delete_resource");
                delete_resource.putObject("message").put("id", prior.getValue().id);
                connection.execute(delete_resource);

                ObjectNode deleteResult = Json.newJsonObject();
                deleteResult.put("result", "uploaded");
                deleteResult.put("file", prior.getKey());
                output.add(deleteResult);
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
    output.out();
  }
}
