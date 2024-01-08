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
package org.adamalang.impl.global;

import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.frontend.Session;
import org.adamalang.frontend.global.GlobalExternNexus;
import org.adamalang.mysql.DataBase;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.mysql.model.Domains;
import org.adamalang.contracts.DomainWithPolicyResolver;
import org.adamalang.contracts.SpacePolicyLocator;
import org.adamalang.contracts.data.DomainWithPolicy;
import org.adamalang.contracts.data.SpacePolicy;
import org.adamalang.web.service.SpaceKeyRequest;
import org.adamalang.web.service.WebConfig;

public class GlobalDomainWithPolicyResolver implements DomainWithPolicyResolver {
  private static final ExceptionLogger LOGGER = ExceptionLogger.FOR(DomainWithPolicyResolver.class);
  private final SimpleExecutor executor;
  private final SpacePolicyLocator spacePolicyLocator;
  private final DataBase dataBase;
  private final WebConfig webConfig;

  public GlobalDomainWithPolicyResolver(SimpleExecutor executor, SpacePolicyLocator spacePolicyLocator, GlobalExternNexus nexus) {
    this.executor = executor;
    this.spacePolicyLocator = spacePolicyLocator;
    this.dataBase = nexus.database;
    this.webConfig = nexus.webBase.config;
  }

  private Domain testForInventedDomain(String domain) {
    for (String suffix : webConfig.globalDomains) {
      if (domain.endsWith("." + suffix)) {
        String space = domain.substring(0, domain.length() - suffix.length() - 1);
        if ("wildcard".equals(space)) {
          return null;
        }
        return new Domain(domain, 0, space, "default-document", null, false, null, null, System.currentTimeMillis());
      }
    }
    return null;
  }

  public void execute(Session session, String domain, Callback<DomainWithPolicy> callback) {
    executor.execute(new NamedRunnable("resolving-domain") {
      @Override
      public void execute() throws Exception {
        try {
          Domain _domainRecord = testForInventedDomain(domain);
          if (_domainRecord == null) {
            _domainRecord = Domains.get(dataBase, domain);
          }
          final Domain domainRecord = _domainRecord;
          if (domainRecord == null) {
            callback.success(new DomainWithPolicy(null, null));
            return;
          }
          if (domainRecord.space == null) {
            callback.success(new DomainWithPolicy(domainRecord, null));
            return;
          }
          spacePolicyLocator.execute(session, domainRecord.space, new Callback<>() {
            @Override
            public void success(SpacePolicy policy) {
              callback.success(new DomainWithPolicy(domainRecord, policy));
            }

            @Override
            public void failure(ErrorCodeException ex) {
              callback.failure(ex);
            }
          });
        } catch (Exception ex) {
          callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DOMAIN_RESOLVE_UNKNOWN_EXCEPTION, ex, LOGGER));
        }
      }
    });
  }

}
