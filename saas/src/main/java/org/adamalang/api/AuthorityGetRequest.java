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
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/**  */
public class AuthorityGetRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String authority;

  public AuthorityGetRequest(final String identity, final AuthenticatedUser who, final String authority) {
    this.identity = identity;
    this.who = who;
    this.authority = authority;
  }

  public static void resolve(ConnectionNexus nexus, JsonRequest request, Callback<AuthorityGetRequest> callback) {
    try {
      final BulkLatch<AuthorityGetRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String authority = request.getString("authority", true, 430095);
      _latch.with(() -> new AuthorityGetRequest(identity, who.get(), authority));
      nexus.identityService.execute(nexus.session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("authorityget-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }
}
