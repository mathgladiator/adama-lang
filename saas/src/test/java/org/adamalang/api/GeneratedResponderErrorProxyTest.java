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
    new AssetIdResponder(responder).error(new ErrorCodeException(1));
    new AssetKeyResponder(responder).error(new ErrorCodeException(2));
    new AuthResultResponder(responder).error(new ErrorCodeException(3));
    new AuthorityListingResponder(responder).error(new ErrorCodeException(4));
    new AutomaticDomainListingResponder(responder).error(new ErrorCodeException(5));
    new CapacityHostResponder(responder).error(new ErrorCodeException(6));
    new CapacityListResponder(responder).error(new ErrorCodeException(7));
    new ClaimResultResponder(responder).error(new ErrorCodeException(8));
    new DataResponder(responder).error(new ErrorCodeException(9));
    new DeveloperResponder(responder).error(new ErrorCodeException(10));
    new DomainListingResponder(responder).error(new ErrorCodeException(11));
    new DomainPolicyResponder(responder).error(new ErrorCodeException(12));
    new DomainRawResponder(responder).error(new ErrorCodeException(13));
    new FinderResultResponder(responder).error(new ErrorCodeException(14));
    new HashedPasswordResponder(responder).error(new ErrorCodeException(15));
    new HostInitResponder(responder).error(new ErrorCodeException(16));
    new InitiationResponder(responder).error(new ErrorCodeException(17));
    new KeyListingResponder(responder).error(new ErrorCodeException(18));
    new KeyPairResponder(responder).error(new ErrorCodeException(19));
    new KeysResponder(responder).error(new ErrorCodeException(20));
    new KeystoreResponder(responder).error(new ErrorCodeException(21));
    new PaymentResponder(responder).error(new ErrorCodeException(22));
    new PlanResponder(responder).error(new ErrorCodeException(23));
    new PlanWithKeysResponder(responder).error(new ErrorCodeException(24));
    new ProgressResponder(responder).error(new ErrorCodeException(25));
    new ReflectionResponder(responder).error(new ErrorCodeException(26));
    new ReplicaResponder(responder).error(new ErrorCodeException(27));
    new RxhtmlResponder(responder).error(new ErrorCodeException(28));
    new SeqResponder(responder).error(new ErrorCodeException(29));
    new SimpleResponder(responder).error(new ErrorCodeException(30));
    new SpaceListingResponder(responder).error(new ErrorCodeException(31));
    new YesResponder(responder).error(new ErrorCodeException(32));
    Assert.assertEquals(528, errorCount.get());
  }
}
