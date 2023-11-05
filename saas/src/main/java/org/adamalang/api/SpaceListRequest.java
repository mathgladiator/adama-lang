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
import org.adamalang.auth.AuthenticatedUser;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;

/** List the spaces available to the user. */
public class SpaceListRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String marker;
  public final Integer limit;

  public SpaceListRequest(final String identity, final AuthenticatedUser who, final String marker, final Integer limit) {
    this.identity = identity;
    this.who = who;
    this.marker = marker;
    this.limit = limit;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<SpaceListRequest> callback) {
    try {
      final BulkLatch<SpaceListRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String marker = request.getString("marker", false, 0);
      final Integer limit = request.getInteger("limit", false, 0);
      _latch.with(() -> new SpaceListRequest(identity, who.get(), marker, limit));
      nexus.identityService.execute(session, identity, who);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("spacelist-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("marker", marker);
    _node.put("limit", limit);
  }
}
