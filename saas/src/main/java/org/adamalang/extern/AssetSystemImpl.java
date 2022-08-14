package org.adamalang.extern;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.connection.Session;
import org.adamalang.net.client.contracts.SimpleEvents;
import org.adamalang.runtime.contracts.AdamaStream;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.transforms.PerSessionAuthenticator;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.io.ConnectionContext;

import java.util.concurrent.atomic.AtomicBoolean;

/** a concrete implementation of the asset system */
public class AssetSystemImpl implements AssetSystem {
  public final ExternNexus nexus;

  public AssetSystemImpl(ExternNexus nexus) {
    this.nexus = nexus;
  }

  @Override
  public void attach(String identity, ConnectionContext context, Key key, NtAsset asset, Callback<Integer> callback) {
    PerSessionAuthenticator authenticator = new PerSessionAuthenticator(nexus, context);
    authenticator.execute(new Session(authenticator), identity, new Callback<AuthenticatedUser>() {
      @Override
      public void success(AuthenticatedUser who) {
        AtomicBoolean responded = new AtomicBoolean(false);
        AdamaStream stream = nexus.adama.connect(who, key.space, key.key, "{}", new SimpleEvents() {
          @Override
          public void connected() { /* don't care */ }

          @Override
          public void delta(String data) { /* don't care */ }

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
        stream.canAttach(new Callback<Boolean>() {
          @Override
          public void success(Boolean value) {
            if (responded.compareAndSet(false, true)) {
              if (value) {
                stream.attach(asset.id, asset.name, asset.contentType, asset.size, asset.md5, asset.sha384, callback);
                stream.close();
              } else {
                callback.failure(new ErrorCodeException(-123));
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
}
