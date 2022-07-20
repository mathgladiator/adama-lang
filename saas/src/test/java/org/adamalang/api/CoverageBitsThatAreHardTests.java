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

import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.io.JsonResponder;
import org.junit.Test;

public class CoverageBitsThatAreHardTests {
  @Test
  public void coverage() {
    BillingUsageResponder responder = new BillingUsageResponder(new JsonResponder() {
      @Override
      public void stream(String json) {

      }

      @Override
      public void finish(String json) {

      }

      @Override
      public void error(ErrorCodeException ex) {

      }
    });
    responder.next(40, 40L, 80L, 160, 320, 640, 0L);
  }
}
