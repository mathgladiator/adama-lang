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
import org.adamalang.contracts.data.DomainWithPolicy;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;

/** Map a domain to another domain */
public class DomainRedirectRequest {
  public final String identity;
  public final AuthenticatedUser who;
  public final String domain;
  public final DomainWithPolicy resolvedDomain;
  public final String destinationDomain;

  public DomainRedirectRequest(final String identity, final AuthenticatedUser who, final String domain, final DomainWithPolicy resolvedDomain, final String destinationDomain) {
    this.identity = identity;
    this.who = who;
    this.domain = domain;
    this.resolvedDomain = resolvedDomain;
    this.destinationDomain = destinationDomain;
  }

  public static void resolve(Session session, GlobalConnectionNexus nexus, JsonRequest request, Callback<DomainRedirectRequest> callback) {
    try {
      final BulkLatch<DomainRedirectRequest> _latch = new BulkLatch<>(nexus.executor, 2, callback);
      final String identity = request.getString("identity", true, 458759);
      final LatchRefCallback<AuthenticatedUser> who = new LatchRefCallback<>(_latch);
      final String domain = request.getString("domain", true, 488444);
      final LatchRefCallback<DomainWithPolicy> resolvedDomain = new LatchRefCallback<>(_latch);
      final String destinationDomain = request.getString("destination-domain", true, 454644);
      _latch.with(() -> new DomainRedirectRequest(identity, who.get(), domain, resolvedDomain.get(), destinationDomain));
      nexus.identityService.execute(session, identity, who);
      nexus.domainService.execute(session, domain, resolvedDomain);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("domainredirect-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.transforms.PerSessionAuthenticator.logInto(who, _node);
    _node.put("domain", domain);
    org.adamalang.contracts.DomainWithPolicyResolver.logInto(resolvedDomain, _node);
    _node.put("destination-domain", destinationDomain);
  }
}
