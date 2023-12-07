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
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.DocumentHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.*;
import org.adamalang.web.contracts.WebJsonStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DocumentHandlerImpl implements DocumentHandler {
  @Override
  public void attach(Arguments.DocumentAttachArgs args, Output.JsonOrError output) throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("offload");
    String identity = args.config.get_string("identity", null);
    String space = args.space;
    String key = args.key;
    String file = args.file;
    String filename = file;
    if (args.name != null) {
      filename = args.name;
    }
    String contentType = Files.probeContentType(new File(args.file).toPath());
    if (args.type != null) {
      contentType = args.type;
    }
    try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
      try (WebSocketClient client = new WebSocketClient(args.config)) {
        try (Connection connection = client.open()) {
          ObjectNode request = Json.newJsonObject();
          request.put("method", "attachment/start");
          request.put("identity", identity);
          request.put("space", space);
          request.put("key", key);
          request.put("filename", filename);
          request.put("content-type", contentType);
          connection.stream(request, (cId, ask) -> {
            executor.execute(new NamedRunnable("name") {
              @Override
              public void execute() throws Exception {
                int sz = ask.get("chunk_request_size").intValue();
                byte[] chunk = new byte[sz];
                try {
                  int rd = input.read(chunk);
                  if (rd > 0) {
                    MessageDigest digest = Hashing.md5();
                    digest.update(chunk, 0, rd);
                    ObjectNode append = Json.newJsonObject();
                    append.put("method", "attachment/append");
                    append.put("upload", cId.intValue());
                    append.put("chunk-md5", Hashing.finishAndEncode(digest));
                    append.put("base64-bytes", Base64.getEncoder().encodeToString(Arrays.copyOfRange(chunk, 0, rd)));
                    connection.execute(append);
                  } else {
                    ObjectNode append = Json.newJsonObject();
                    append.put("method", "attachment/finish");
                    append.put("upload", cId.intValue());
                    output.add(connection.execute(append));
                    output.out();
                    connection.close();
                    executor.shutdown();
                  }
                } catch (Exception ioe) {
                  executor.shutdown();
                }
              }
            });
          });
        }
      }
    }
  }

  @Override
  public void connect(Arguments.DocumentConnectArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/create");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("key", args.key);
        connection.stream(request, (cId, response) -> {
          System.out.println(response.toPrettyString());
          // NOTE: due to the streaming nature, we really can't do much else
        });
      }
    }
  }

  @Override
  public void downloadArchive(Arguments.DocumentDownloadArchiveArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "document/download-archive");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("key", args.key);

        File archiveFileTemp = new File(args.output + ".temp");
        File archiveFileFinal = new File(args.output);
        FileOutputStream outputStream = new FileOutputStream(archiveFileTemp);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean alive = new AtomicBoolean(true);
        connection.raw().execute(request, new WebJsonStream() {
          @Override
          public void data(int cId, ObjectNode response) {
            byte[] base64 = Base64.getDecoder().decode(response.get("base64Bytes").textValue());
            String md5 = response.get("chunkMd5").textValue();
            MessageDigest digest = Hashing.md5();
            digest.update(base64);
            if (!Hashing.finishAndEncode(digest).equals(md5)) {
              throw new RuntimeException("corruption during transit");
            }
            try {
              outputStream.write(base64);
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            }
          }

          @Override
          public void complete() {
            try {
              outputStream.flush();
              outputStream.close();
              Files.move(archiveFileTemp.toPath(), archiveFileFinal.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException ex) {
              throw new RuntimeException(ex);
            } finally {
              alive.set(false);
              latch.countDown();
            }
          }

          @Override
          public void failure(int code) {
            alive.set(false);
            latch.countDown();
            throw new RuntimeException("failed downloading:" + code);
          }
        });
        while (alive.get()) {
          // FUN progress token?
          latch.await(1000, TimeUnit.MILLISECONDS);
        }
      }
    }
  }

  @Override
  public void create(Arguments.DocumentCreateArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    ObjectNode argNode = Json.parseJsonObject(args.arg);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "document/create");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("key", args.key);
        if (args.entropy != null) {
          request.put("entropy", args.entropy);
        }
        request.set("arg", argNode);
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void delete(Arguments.DocumentDeleteArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "document/delete");
        request.put("identity", identity);
        request.put("space", args.space);
        request.put("key", args.key);
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void list(Arguments.DocumentListArgs args, Output.JsonOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    int limit = Integer.parseInt(args.limit);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "document/list");
        request.put("identity", identity);
        request.put("space", args.space);
        if (args.marker != null) {
          request.put("marker", args.marker);
        }
        request.put("limit", limit);
        connection.stream(request, (cId, item) -> {
          output.add(item);
        });
        output.out();
      }
    }
  }
}
