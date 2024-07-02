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
package org.adamalang.devbox;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.Streamback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.ConnectionMode;
import org.adamalang.runtime.sys.CoreRequestContext;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.CoreStream;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.io.ConnectionContext;
import org.checkerframework.checker.units.qual.K;

import java.io.File;
import java.nio.file.Files;

public class LocalAssets implements AssetSystem {
  private final TerminalIO io;
  private final File root;
  private final CoreService service;

  public LocalAssets(TerminalIO io, File root, CoreService service) throws Exception {
    this.io = io;
    this.root = root;
    this.service = service;
    this.root.mkdirs();
    if (!(root.exists() && root.isDirectory())) {
      throw new Exception("root '" + root.getName() + "' either doesn't exist or is not a directory");
    }
  }

  @Override
  public void request(AssetRequest request, AssetStream stream) {
    try {
      File path = new File(root, request.space + "/" + request.key);
      if (!path.exists()) {
        stream.failure(404);
        return;
      }
      JsonStreamReader reader = new JsonStreamReader(Files.readString(new File(path, request.id + ".meta").toPath()));
      NtAsset asset = reader.readNtAsset();
      request(new Key(request.space, request.key), asset, stream);
    } catch (Exception ex) {
      ex.printStackTrace();
      stream.failure(42);
    }
  }

  @Override
  public void request(Key key, NtAsset asset, AssetStream stream) {
    File data = new File(root, key.space + "/" + key.key + "/" + asset.id + ".data");
    if (!data.exists()) {
      stream.failure(404);
      return;
    }
    io.info("serving asset:" + asset.id + "; for " + key.space + "/" + key.key);
    stream.headers(asset.size, asset.contentType, asset.md5);
    try {
      byte[] all = Files.readAllBytes(data.toPath());
      stream.body(all, 0, all.length, true);
    } catch (Exception ex) {
      stream.failure(500);
    }
  }

  @Override
  public void attach(String identity, ConnectionContext context, Key key, NtAsset asset, String channel, String message, Callback<Integer> callback) {
    NtPrincipal who = LocalAdama.principalOf(identity);
    service.connect(new CoreRequestContext(who, context.origin, context.remoteIp, key.key), key, "{}", ConnectionMode.WriteOnly, new Streamback() {
      CoreStream _stream;
      boolean _responded = false;

      @Override
      public void onSetupComplete(CoreStream stream) {
        _stream = stream;
      }

      @Override
      public void status(StreamStatus status) {
        if (status == StreamStatus.Connected && !_responded) {
          _responded = true;
          io.info("attachments|asking can_attach: " + key.space + "/" + key.key + "; asset.id=" + asset.id);
          _stream.canAttach(new Callback<>() {
            @Override
            public void success(Boolean can) {
              if (can) {
                io.info("attachments|granted! now attaching: " + key.space + "/" + key.key + "; asset.id=" + asset.id);
                _stream.attach(asset.id, asset.name, asset.contentType, asset.size, asset.md5, asset.sha384, new Callback<Integer>() {
                  @Override
                  public void success(Integer value) {
                    if (channel != null) {
                      io.info("attachments|attached! now sending messsage: " + key.space + "/" + key.key + "; asset.id=" + asset.id + "; message=" + message);
                      _stream.send(channel, null, message, new Callback<Integer>() {
                        @Override
                        public void success(Integer value) {
                          io.info("attachments|file upload was complete success: " + key.space + "/" + key.key + "; asset.id=" + asset.id + "; message=" + message);
                          callback.success(value);
                        }

                        @Override
                        public void failure(ErrorCodeException ex) {
                          io.error("attachments|failed-sending; ex.code = " + ex.code);
                          callback.failure(ex);
                        }
                      });
                    } else {
                      io.info("attachments|file upload was success: " + key.space + "/" + key.key + "; asset.id=" + asset.id);
                      callback.success(value);
                    }
                  }

                  @Override
                  public void failure(ErrorCodeException ex) {
                    io.error("attachments|failed-attachment; ex.code = " + ex.code);
                    callback.failure(ex);
                  }
                });
              }
            }

            @Override
            public void failure(ErrorCodeException ex) {
              io.error("attachments|failed-can-attach; ex.code = " + ex.code);
              callback.failure(ex);
            }
          });
        }
      }

      @Override
      public void next(String data) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        io.error("attachments|failed-connecting; ex.code = " + ex.code);
        callback.failure(ex);
      }
    });
  }

  @Override
  public void upload(Key key, NtAsset asset, AssetUploadBody body, Callback<Void> callback) {
    try {
      File path = new File(root, key.space + "/" + key.key);
      path.mkdirs();
      JsonStreamWriter writer = new JsonStreamWriter();
      writer.writeNtAsset(asset);
      Files.writeString(new File(path, asset.id + ".meta").toPath(), writer.toString());
      File assetData = new File(path, asset.id + ".data");
      File localFile = body.getFileIfExists();
      if (localFile != null) {
        Files.move(localFile.toPath(), assetData.toPath());
      } else {
        Files.write(assetData.toPath(), body.getBytes());
      }
      io.info("uploaded asset; id=" + asset.id + "; content-type=" + asset.contentType + "; for=" + key.space + "/" + key.key);
      callback.success(null);
    } catch (Exception ex) {
      ex.printStackTrace();
      callback.failure(new ErrorCodeException(42));
    }
  }

  public void write(Key key, NtAsset asset, byte[] memory) throws Exception {
    File path = new File(root, key.space + "/" + key.key);
    path.mkdirs();
    JsonStreamWriter writer = new JsonStreamWriter();
    writer.writeNtAsset(asset);
    Files.writeString(new File(path, asset.id + ".meta").toPath(), writer.toString());
    File assetData = new File(path, asset.id + ".data");
    Files.write(assetData.toPath(), memory);
  }
}
