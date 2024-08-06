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
import org.adamalang.contracts.data.DomainWithPolicy;
import org.adamalang.frontend.Session;
import org.adamalang.validators.ValidateDomain;
import org.adamalang.web.io.*;

/** Authorize a username and password against a document, and set a new password */
public class DocumentAuthorizeDomainWithResetRequest {
  public final String domain;
  public final DomainWithPolicy resolvedDomain;
  public final String username;
  public final String password;
  public final String new_password;

  public DocumentAuthorizeDomainWithResetRequest(final String domain, final DomainWithPolicy resolvedDomain, final String username, final String password, final String new_password) {
    this.domain = domain;
    this.resolvedDomain = resolvedDomain;
    this.username = username;
    this.password = password;
    this.new_password = new_password;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<DocumentAuthorizeDomainWithResetRequest> callback) {
    try {
      final BulkLatch<DocumentAuthorizeDomainWithResetRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String domain = request.getStringNormalize("domain", true, 488444);
      ValidateDomain.validate(domain);
      final LatchRefCallback<DomainWithPolicy> resolvedDomain = new LatchRefCallback<>(_latch);
      final String username = request.getString("username", true, 458737);
      final String password = request.getString("password", true, 465917);
      final String new_password = request.getString("new_password", true, 466931);
      _latch.with(() -> new DocumentAuthorizeDomainWithResetRequest(domain, resolvedDomain.get(), username, password, new_password));
      nexus.domainService.execute(session, domain, resolvedDomain);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentauthorizedomainwithreset-error") {
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
