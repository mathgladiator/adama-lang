/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
    new AuthorityListingResponder(responder).error(new ErrorCodeException(3));
    new AutomaticDomainListingResponder(responder).error(new ErrorCodeException(4));
    new BillingUsageResponder(responder).error(new ErrorCodeException(5));
    new ClaimResultResponder(responder).error(new ErrorCodeException(6));
    new DataResponder(responder).error(new ErrorCodeException(7));
    new DomainListingResponder(responder).error(new ErrorCodeException(8));
    new DomainPolicyResponder(responder).error(new ErrorCodeException(9));
    new HashedPasswordResponder(responder).error(new ErrorCodeException(10));
    new InitiationResponder(responder).error(new ErrorCodeException(11));
    new KeyListingResponder(responder).error(new ErrorCodeException(12));
    new KeyPairResponder(responder).error(new ErrorCodeException(13));
    new KeystoreResponder(responder).error(new ErrorCodeException(14));
    new PaymentResponder(responder).error(new ErrorCodeException(15));
    new PlanResponder(responder).error(new ErrorCodeException(16));
    new ProgressResponder(responder).error(new ErrorCodeException(17));
    new ReflectionResponder(responder).error(new ErrorCodeException(18));
    new ReplicaResponder(responder).error(new ErrorCodeException(19));
    new RxhtmlResponder(responder).error(new ErrorCodeException(20));
    new SeqResponder(responder).error(new ErrorCodeException(21));
    new SimpleResponder(responder).error(new ErrorCodeException(22));
    new SpaceListingResponder(responder).error(new ErrorCodeException(23));
    new YesResponder(responder).error(new ErrorCodeException(24));
    Assert.assertEquals(300, errorCount.get());
  }
}
