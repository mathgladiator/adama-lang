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
