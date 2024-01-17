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
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.JsonResponder;;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class GeneratedResponderErrorProxyTest {
  @Test
  public void proxy() throws Exception {
    AtomicInteger errorCount = new AtomicInteger(0);
    JsonResponder responder = new JsonResponder() {
      @Override
      public void stream(String json) {

      }

      @Override
      public void finish(String json) {

      }

      @Override
      public void error(ErrorCodeException ex) {
        errorCount.addAndGet(ex.code);
      }
    };
    new AccessPolicyResponder(responder).error(new ErrorCodeException(1));
    new AssetIdResponder(responder).error(new ErrorCodeException(2));
    new AuthResultResponder(responder).error(new ErrorCodeException(3));
    new AuthorityListingResponder(responder).error(new ErrorCodeException(4));
    new AutomaticDomainListingResponder(responder).error(new ErrorCodeException(5));
    new BackupStreamResponder(responder).error(new ErrorCodeException(6));
    new CapacityHostResponder(responder).error(new ErrorCodeException(7));
    new CapacityListResponder(responder).error(new ErrorCodeException(8));
    new ClaimResultResponder(responder).error(new ErrorCodeException(9));
    new DataResponder(responder).error(new ErrorCodeException(10));
    new DeveloperResponder(responder).error(new ErrorCodeException(11));
    new DomainListingResponder(responder).error(new ErrorCodeException(12));
    new DomainPolicyResponder(responder).error(new ErrorCodeException(13));
    new DomainRawResponder(responder).error(new ErrorCodeException(14));
    new DomainVapidResponder(responder).error(new ErrorCodeException(15));
    new DomainVerifyResponder(responder).error(new ErrorCodeException(16));
    new FinderResultResponder(responder).error(new ErrorCodeException(17));
    new HashedPasswordResponder(responder).error(new ErrorCodeException(18));
    new HostInitResponder(responder).error(new ErrorCodeException(19));
    new IdentityHashResponder(responder).error(new ErrorCodeException(20));
    new InitiationResponder(responder).error(new ErrorCodeException(21));
    new KeyListingResponder(responder).error(new ErrorCodeException(22));
    new KeyPairResponder(responder).error(new ErrorCodeException(23));
    new KeysResponder(responder).error(new ErrorCodeException(24));
    new KeystoreResponder(responder).error(new ErrorCodeException(25));
    new MetricsAggregateResponder(responder).error(new ErrorCodeException(26));
    new PaymentResponder(responder).error(new ErrorCodeException(27));
    new PlanResponder(responder).error(new ErrorCodeException(28));
    new PlanWithKeysResponder(responder).error(new ErrorCodeException(29));
    new ProgressResponder(responder).error(new ErrorCodeException(30));
    new ReflectionResponder(responder).error(new ErrorCodeException(31));
    new ReplicaResponder(responder).error(new ErrorCodeException(32));
    new RxhtmlResponder(responder).error(new ErrorCodeException(33));
    new SeqResponder(responder).error(new ErrorCodeException(34));
    new SimpleResponder(responder).error(new ErrorCodeException(35));
    new SpaceListingResponder(responder).error(new ErrorCodeException(36));
    new StatsResponder(responder).error(new ErrorCodeException(37));
    new TokenStreamResponder(responder).error(new ErrorCodeException(38));
    new YesResponder(responder).error(new ErrorCodeException(39));
    Assert.assertEquals(780, errorCount.get());
  }
}
