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
        callback.success(new Domain(raw.domain, raw.owner, raw.space, raw.key, raw.forward, raw.route, raw.certificate, new Date(raw.timestamp), raw.timestamp, raw.configured));
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    });
  }
}
