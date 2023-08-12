/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.contracts.data.DomainWithPolicy;
import org.adamalang.frontend.Session;
import org.adamalang.web.io.*;

/** Authorize a username and password against a document. */
public class DocumentAuthorizeDomainRequest {
  public final String domain;
  public final DomainWithPolicy resolvedDomain;
  public final String username;
  public final String password;

  public DocumentAuthorizeDomainRequest(final String domain, final DomainWithPolicy resolvedDomain, final String username, final String password) {
    this.domain = domain;
    this.resolvedDomain = resolvedDomain;
    this.username = username;
    this.password = password;
  }

  public static void resolve(Session session, RegionConnectionNexus nexus, JsonRequest request, Callback<DocumentAuthorizeDomainRequest> callback) {
    try {
      final BulkLatch<DocumentAuthorizeDomainRequest> _latch = new BulkLatch<>(nexus.executor, 1, callback);
      final String domain = request.getString("domain", true, 488444);
      final LatchRefCallback<DomainWithPolicy> resolvedDomain = new LatchRefCallback<>(_latch);
      final String username = request.getString("username", true, 458737);
      final String password = request.getString("password", true, 465917);
      _latch.with(() -> new DocumentAuthorizeDomainRequest(domain, resolvedDomain.get(), username, password));
      nexus.domainService.execute(session, domain, resolvedDomain);
    } catch (ErrorCodeException ece) {
      nexus.executor.execute(new NamedRunnable("documentauthorizedomain-error") {
        @Override
        public void execute() throws Exception {
          callback.failure(ece);
        }
      });
    }
  }

  public void logInto(ObjectNode _node) {
    org.adamalang.contracts.DomainWithPolicyResolver.logInto(resolvedDomain, _node);
  }
}
