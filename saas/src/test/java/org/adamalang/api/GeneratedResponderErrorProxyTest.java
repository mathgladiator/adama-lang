/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
    new BillingUsageResponder(responder).error(new ErrorCodeException(3));
    new ClaimResultResponder(responder).error(new ErrorCodeException(4));
    new DataResponder(responder).error(new ErrorCodeException(5));
    new InitiationResponder(responder).error(new ErrorCodeException(6));
    new KeyResponder(responder).error(new ErrorCodeException(7));
    new KeyListingResponder(responder).error(new ErrorCodeException(8));
    new KeystoreResponder(responder).error(new ErrorCodeException(9));
    new PlanResponder(responder).error(new ErrorCodeException(10));
    new ProgressResponder(responder).error(new ErrorCodeException(11));
    new ReflectionResponder(responder).error(new ErrorCodeException(12));
    new SeqResponder(responder).error(new ErrorCodeException(13));
    new SimpleResponder(responder).error(new ErrorCodeException(14));
    new SpaceListingResponder(responder).error(new ErrorCodeException(15));
    Assert.assertEquals(120, errorCount.get());
  }
}
