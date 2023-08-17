/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.impl.region;

import org.adamalang.api.ClientDomainRawResponse;
import org.adamalang.api.ClientRegionalDomainLookupRequest;
import org.adamalang.api.SelfClient;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.runtime.sys.domains.Domain;

import java.sql.Date;

/** find a domain from the global region */
public class RegionDomainFinder implements DomainFinder {
  private final SelfClient client;
  private final String identity;

  public RegionDomainFinder(SelfClient client, String identity) {
    this.client = client;
    this.identity = identity;
  }

  @Override
  public void find(String domain, Callback<Domain> callback) {
    ClientRegionalDomainLookupRequest request = new ClientRegionalDomainLookupRequest();
    request.identity = identity;
    request.domain = domain;
    client.regionalDomainLookup(request, new Callback<>() {
      @Override
      public void success(ClientDomainRawResponse raw) {
        callback.success(new Domain(raw.domain, raw.owner, raw.space, raw.key, raw.route, raw.certificate, new Date(raw.timestamp), raw.timestamp));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }
}
