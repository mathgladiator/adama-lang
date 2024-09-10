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
package org.adamalang.frontend.global;

import org.adamalang.auth.Authenticator;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.frontend.Session;
import org.adamalang.extern.aws.S3;
import org.adamalang.multiregion.MultiRegionClient;
import org.adamalang.mysql.DataBase;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.impl.global.GlobalPerSessionAuthenticator;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.web.assets.AssetRequest;
import org.adamalang.web.assets.AssetStream;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.assets.AssetUploadBody;
import org.adamalang.web.io.ConnectionContext;

import java.util.concurrent.atomic.AtomicBoolean;

/** a concrete implementation of the asset system */
public class GlobalAssetSystem implements AssetSystem {
  public final DataBase database;
  public final String masterKey;
  public final MultiRegionClient adama;
  public final S3 s3;
  public final Authenticator authenticator;

  public GlobalAssetSystem(DataBase database, String masterKey, Authenticator authenticator, MultiRegionClient adama, S3 s3) {
    this.database = database;
    this.masterKey = masterKey;
    this.authenticator = authenticator;
    this.adama = adama;
    this.s3 = s3;
  }

  @Override
  public void request(AssetRequest request, AssetStream stream) {
    s3.request(request, stream);
  }

  @Override
  public void request(Key key, NtAsset asset, AssetStream stream) {
    AssetRequest request = new AssetRequest(key.space, key.key, asset.id);
    s3.request(request, stream);
  }

  @Override
  public void attach(String identity, ConnectionContext context, Key key, NtAsset asset, String channel, String message, Callback<Integer> callback) {
    PerSessionAuthenticator sessionAuthenticator = new GlobalPerSessionAuthenticator(database, authenticator, context, new String[] {}, new String[] {});
    sessionAuthenticator.execute(new Session(sessionAuthenticator), identity, new Callback<AuthenticatedUser>() {
      @Override
      public void success(AuthenticatedUser who) {
        AtomicBoolean responded = new AtomicBoolean(false);
        AdamaStream stream = adama.connect(who, key.space, key.key, "{}", new SimpleEvents() {
          @Override
          public void connected() {
            /* don't care */
          }

          @Override
          public void delta(String data) { /* don't care */ }

          @Override
          public void traffic(String trafficHint) {
          }

          @Override
          public void error(int code) {
            if (responded.compareAndSet(false, true)) {
              callback.failure(new ErrorCodeException(code));
            }
          }

          @Override
          public void disconnected() {
            if (responded.compareAndSet(false, true)) {
              callback.failure(new ErrorCodeException(-123));
            }
          }
        });
        stream.canAttach(new Callback<>() {
          @Override
          public void success(Boolean value) {
            if (value) {
              stream.attach(asset.id, asset.name, asset.contentType, asset.size, asset.md5, asset.sha384, new Callback<Integer>() {
                @Override
                public void success(Integer value) {
                  if (channel != null) {
                    stream.send(channel, null, message, new Callback<Integer>() {
                      @Override
                      public void success(Integer value) {
                        if (responded.compareAndSet(false, true)) {
                          callback.success(value);
                          stream.close();
                        }
                      }

                      @Override
                      public void failure(ErrorCodeException ex) {
                        if (responded.compareAndSet(false, true)) {
                          callback.failure(ex);
                          stream.close();
                        }
                      }
                    });
                  } else {
                    if (responded.compareAndSet(false, true)) {
                      callback.success(value);
                      stream.close();
                    }
                  }
                }

                @Override
                public void failure(ErrorCodeException ex) {
                  if (responded.compareAndSet(false, true)) {
                    callback.failure(ex);
                    stream.close();
                  }
                }
              });
            } else {
              if (responded.compareAndSet(false, true)) {
                callback.failure(new ErrorCodeException(-123));
                stream.close();
              }
            }
          }

          @Override
          public void failure(ErrorCodeException ex) {
            if (responded.compareAndSet(false, true)) {
              callback.failure(ex);
              stream.close();
            }
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }

  @Override
  public void upload(Key key, NtAsset asset, AssetUploadBody body, Callback<Void> callback) {
    s3.upload(key, asset, body, callback);
  }

}
