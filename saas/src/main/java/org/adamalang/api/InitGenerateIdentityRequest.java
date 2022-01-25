/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.api;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.connection.Session;
import org.adamalang.web.io.*;

/** When the developer receives an email, this method is invoked to complete the hand-shake.
  * 
  * The server will generate a key-pair and send the secret to the client to stash within their config, and the
  * public key will be stored to validate future requests made by this developer machine.
  * 
  * A public key will be held onto for 30 days. */
public class InitGenerateIdentityRequest {
  public final Long connection;
  public final Boolean revoke;
  public final String code;

  public InitGenerateIdentityRequest(final Long connection, final Boolean revoke, final String code) {
    this.connection = connection;
    this.revoke = revoke;
    this.code = code;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<InitGenerateIdentityRequest> callback) {
    try {
      final Long connection = request.getLong("connection", true, 405505);
      final Boolean revoke = request.getBoolean("revoke", false, 0);
      final String code = request.getString("code", true, 455681);
      nexus.executor.execute(() -> {
        callback.success(new InitGenerateIdentityRequest(connection, revoke, code));
      });
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(() -> {
        callback.failure(ece);
      });
    }
  }
}
