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
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateKeystore;
import org.adamalang.web.io.*;

/** Set the public keystore for the authority. */
public class AuthoritySetRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String authority;
  public final ObjectNode keyStore;

  public AuthoritySetRequest(final String identity, final AuthenticatedUser who, final String authority, final ObjectNode keyStore) {
    this.identity = identity;
    this.who = who;
    this.authority = authority;
    this.keyStore = keyStore;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<AuthoritySetRequest> callback) {
    try {
      final BulkLatch<AuthoritySetRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String authority = request.getString("authority", true, 430095);
      final ObjectNode keyStore = request.getObject("key-store", true, 457743);
      ValidateKeystore.validate(keyStore);
      _latch.with(() -> new AuthoritySetRequest(identity, who.get(), authority, keyStore));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("authorityset-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("authority", authority);
  }
}
