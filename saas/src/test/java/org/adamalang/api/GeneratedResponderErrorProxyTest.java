/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
    new AssetKeyResponder(responder).error(new ErrorCodeException(1));
    new AuthorityListingResponder(responder).error(new ErrorCodeException(2));
    new AutomaticDomainListingResponder(responder).error(new ErrorCodeException(3));
    new BillingUsageResponder(responder).error(new ErrorCodeException(4));
    new ClaimResultResponder(responder).error(new ErrorCodeException(5));
    new DataResponder(responder).error(new ErrorCodeException(6));
    new DomainPolicyResponder(responder).error(new ErrorCodeException(7));
    new InitiationResponder(responder).error(new ErrorCodeException(8));
    new KeyListingResponder(responder).error(new ErrorCodeException(9));
    new KeyPairResponder(responder).error(new ErrorCodeException(10));
    new KeystoreResponder(responder).error(new ErrorCodeException(11));
    new PaymentResponder(responder).error(new ErrorCodeException(12));
    new PlanResponder(responder).error(new ErrorCodeException(13));
    new ProgressResponder(responder).error(new ErrorCodeException(14));
    new ReflectionResponder(responder).error(new ErrorCodeException(15));
    new RxhtmlResponder(responder).error(new ErrorCodeException(16));
    new SeqResponder(responder).error(new ErrorCodeException(17));
    new SimpleResponder(responder).error(new ErrorCodeException(18));
    new SpaceListingResponder(responder).error(new ErrorCodeException(19));
    new YesResponder(responder).error(new ErrorCodeException(20));
    Assert.assertEquals(210, errorCount.get());
  }
}
