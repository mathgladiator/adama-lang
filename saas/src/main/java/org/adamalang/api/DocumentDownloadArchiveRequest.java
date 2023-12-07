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
import org.adamalang.contracts.data.SpacePolicy;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateKey;
import org.adamalang.validators.ValidateSpace;
import org.adamalang.web.io.*;

/** Send an authorization request to the document */
public class DocumentDownloadArchiveRequest {
  public final String space;
  public final SpacePolicy policy;
  public final String key;

  public DocumentDownloadArchiveRequest(final String space, final SpacePolicy policy, final String key) {
    this.space = space;
    this.policy = policy;
    this.key = key;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<DocumentDownloadArchiveRequest> callback) {
    try {
      final BulkLatch<DocumentDownloadArchiveRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String space = request.getStringNormalize("space", true, 461828);
      ValidateSpace.validate(space);
      final LatchRefCallback<SpacePolicy> policy = new LatchRefCallback<>(_latch);
      final String key = request.getString("key", true, 466947);
      ValidateKey.validate(key);
      _latch.with(() -> new DocumentDownloadArchiveRequest(space, policy.get(), key));
      nexus.spaceService.execute(session, space, policy);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentdownloadarchive-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("space", space);
    org.adamalang.contracts.SpacePolicyLocator.logInto(policy, _node);
    _node.put("key", key);
  }
}
