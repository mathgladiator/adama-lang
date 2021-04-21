/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
