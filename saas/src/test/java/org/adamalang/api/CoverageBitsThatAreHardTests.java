/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    responder.next(40, 40L, 80L, 160, 320, 640, 0L, 10L, 123L, 345L);
  }
}
