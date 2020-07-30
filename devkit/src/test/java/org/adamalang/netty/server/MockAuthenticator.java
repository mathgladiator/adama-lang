/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import org.adamalang.netty.api.AdamaSession;
import org.adamalang.netty.contracts.AuthCallback;
import org.adamalang.netty.contracts.Authenticator;
import org.adamalang.runtime.natives.NtClient;

public class MockAuthenticator implements Authenticator {
  @Override
  public void authenticate(final String token, final AuthCallback callback) {
    if ("XOK".equals(token)) {
      callback.success(new AdamaSession(NtClient.NO_ONE));
    } else {
      if (token.equals("crash")) { throw new UnsupportedOperationException(); }
      callback.failure();
    }
  }

  @Override
  public void close() {
  }
}
