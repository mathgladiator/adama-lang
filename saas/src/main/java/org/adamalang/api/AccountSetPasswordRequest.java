/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.connection.Session;
import org.adamalang.transforms.results.AuthenticatedUser;
import org.adamalang.web.io.*;

/** Set the password for an Adama developer. */
public class AccountSetPasswordRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String password;

  public AccountSetPasswordRequest(final String identity, final AuthenticatedUser who, final String password) {
    this.identity = identity;
    this.who = who;
    this.password = password;
  }

  public static void resolve(Session session, ConnectionNexus nexus, JsonRequest request, Callback<AccountSetPasswordRequest> callback) {
    try {
      final BulkLatch<AccountSetPasswordRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String password = request.getString("password", true, 465917);
      _latch.with(() -> new AccountSetPasswordRequest(identity, who.get(), password));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("accountsetpassword-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
  }
}
