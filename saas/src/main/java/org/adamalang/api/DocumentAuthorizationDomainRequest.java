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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.contracts.data.DomainWithPolicy;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateDomain;
import org.adamalang.web.io.*;

/** Send an authorization request to a document via a domain */
public class DocumentAuthorizationDomainRequest {
  public final String domain;
  public final DomainWithPolicy resolvedDomain;
  public final JsonNode message;

  public DocumentAuthorizationDomainRequest(final String domain, final DomainWithPolicy resolvedDomain, final JsonNode message) {
    this.domain = domain;
    this.resolvedDomain = resolvedDomain;
    this.message = message;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<DocumentAuthorizationDomainRequest> callback) {
    try {
      final BulkLatch<DocumentAuthorizationDomainRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String domain = request.getStringNormalize("domain", true, 488444);
      ValidateDomain.validate(domain);
      final LatchRefCallback<DomainWithPolicy> resolvedDomain = new LatchRefCallback<>(_latch);
      final JsonNode message = request.getJsonNode("message", true, 425987);
      _latch.with(() -> new DocumentAuthorizationDomainRequest(domain, resolvedDomain.get(), message));
      nexus.domainService.execute(session, domain, resolvedDomain);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentauthorizationdomain-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    _node.put("domain", domain);
    org.adamalang.contracts.DomainWithPolicyResolver.logInto(resolvedDomain, _node);
  }
}
