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
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateEmail;
import org.adamalang.web.io.*;

/** This establishes a developer machine via email verification.
  * 
  * Copy the code from the email into this request.
  * 
  * The server will generate a key-pair and send the secret to the client to stash within their config, and the
  * public key will be stored to validate future requests made by this developer machine.
  * 
  * A public key will be held onto for 30 days. */
public class InitCompleteAccountRequest {
  public final String email;
  public final Integer userId;
  public final Boolean revoke;
  public final String code;

  public InitCompleteAccountRequest(final String email, final Integer userId, final Boolean revoke, final String code) {
    this.email = email;
    this.userId = userId;
    this.revoke = revoke;
    this.code = code;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<InitCompleteAccountRequest> callback) {
    try {
      final BulkLatch<InitCompleteAccountRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String email = request.getString("email", true, 473103);
      ValidateEmail.validate(email);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      final Boolean revoke = request.getBoolean("revoke", false, 0);
      final String code = request.getString("code", true, 455681);
      _latch.with(() -> new InitCompleteAccountRequest(email, userId.get(), revoke, code));
      nexus.emailService.execute(session, email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("initcompleteaccount-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("email", email);
    org.adamalang.contracts.UserIdResolver.logInto(userId, _node);
    _node.put("revoke", revoke);
  }
}
