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
package org.adamalang.web.service;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.cache.AsyncSharedLRUCache;
import org.adamalang.common.cache.Measurable;
import org.adamalang.common.cache.SyncCacheLRU;
import org.adamalang.runtime.sys.domains.Domain;
import org.adamalang.runtime.sys.domains.DomainFinder;
import org.adamalang.web.contracts.CertificateFinder;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

public class CertificateBoot {
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(CertificateBoot.class);

  public static class MeasuredSslContext implements Measurable {
    public final SslContext context;

    public MeasuredSslContext(SslContext context) {
      this.context = context;
    }

    @Override
    public long measure() {
      return 1;
    }
  }

  public static CertificateFinder make(WebConfig webConfig, DomainFinder df, SimpleExecutor executor) {
    SyncCacheLRU<String, MeasuredSslContext> realCache = new SyncCacheLRU<>(TimeSource.REAL_TIME, webConfig.minDomainsToHoldTo, webConfig.maxDomainsToHoldTo, webConfig.maxDomainsToHoldTo * 2, webConfig.maxDomainAge, (domain, context) -> {});
    AsyncSharedLRUCache<String, MeasuredSslContext> cache = new AsyncSharedLRUCache<>(executor, realCache, (domain, callback) -> {
      df.find(domain, new Callback<Domain>() {
        @Override
        public void success(Domain lookup) {
          try {
            if (lookup != null && lookup.certificate != null) {
              ObjectNode certificate = Json.parseJsonObject(lookup.certificate);
              ByteArrayInputStream keyInput = new ByteArrayInputStream(certificate.get("key").textValue().getBytes(StandardCharsets.UTF_8));
              ByteArrayInputStream certInput = new ByteArrayInputStream(certificate.get("cert").textValue().getBytes(StandardCharsets.UTF_8));
              SslContext contextToUse = SslContextBuilder.forServer(certInput, keyInput).build();
              callback.success(new MeasuredSslContext(contextToUse));
            } else {
              callback.failure(new ErrorCodeException(ErrorCodes.DOMAIN_TRANSLATE_FAILURE));
            }
          } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.DOMAIN_LOOKUP_FAILURE, ex, EXLOGGER));
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    });
    return (rawDomain, callback) -> {
      String _domainToLookup = rawDomain;
      { // hyper fast optimistic path
        if (_domainToLookup == null) { // no SNI provided -> use default
          callback.success(null);
          return;
        }
        if (!webConfig.specialDomains.contains(_domainToLookup)) {
          if (_domainToLookup.endsWith("." + webConfig.regionalDomain)) { // the regional domain -> use default
            callback.success(null);
            return;
          }
          for (String globalDomain : webConfig.globalDomains) {
            if (_domainToLookup.endsWith("." + globalDomain)) {
              _domainToLookup = "wildcard." + globalDomain;
              break;
            }
          }
        }
      }

      cache.get(_domainToLookup, new Callback<MeasuredSslContext>() {
        @Override
        public void success(MeasuredSslContext value) {
          callback.success(value.context);
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      });
    };
  }
}
