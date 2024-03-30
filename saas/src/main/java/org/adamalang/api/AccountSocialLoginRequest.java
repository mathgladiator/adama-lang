/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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

/** Sign an Adama user in with an email and password pair. */
public class AccountSocialLoginRequest {
  public final String email;
  public final Integer userId;
  public final String password;
  public final String scopes;

  public AccountSocialLoginRequest(final String email, final Integer userId, final String password, final String scopes) {
    this.email = email;
    this.userId = userId;
    this.password = password;
    this.scopes = scopes;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<AccountSocialLoginRequest> callback) {
    try {
      final BulkLatch<AccountSocialLoginRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String email = request.getString("email", true, 473103);
      ValidateEmail.validate(email);
      final LatchRefCallback<Integer> userId = new LatchRefCallback<>(_latch);
      final String password = request.getString("password", true, 465917);
      final String scopes = request.getString("scopes", true, 463103);
      _latch.with(() -> new AccountSocialLoginRequest(email, userId.get(), password, scopes));
      nexus.emailService.execute(session, email, userId);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("accountsociallogin-error") {
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
    _node.put("scopes", scopes);
  }
}
