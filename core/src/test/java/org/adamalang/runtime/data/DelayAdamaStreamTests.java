/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.mocks.MockAdamaStream;
import org.junit.Assert;
import org.junit.Test;

public class DelayAdamaStreamTests {
  @Test
  public void just_delay() {
    DelayAdamaStream delay = new DelayAdamaStream(SimpleExecutor.NOW, new NoOpMetricsFactory().makeItemActionMonitor("xyz"));
    delay.attach("id", "name", "type", 1000, "md5", "sha", Callback.DONT_CARE_INTEGER);
    delay.canAttach(new Callback<Boolean>() {
      @Override
      public void success(Boolean value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    delay.update("UPDATE!");
    delay.send("channel", "marker", "message", Callback.DONT_CARE_INTEGER);
    delay.send("failure", "marker", "message", Callback.DONT_CARE_INTEGER);
    delay.close();
    MockAdamaStream stream = new MockAdamaStream();
    delay.ready(stream);
    delay.unready();
    Assert.assertEquals("ATTACH:id/name/type/1000/md5/sha\n" + "CANATTACH\n" + "UPDATE:UPDATE!\n" + "SEND:channel/marker/message\n" + "SEND:failure/marker/message\n" + "CLOSE\n", stream.toString());
  }

  @Test
  public void errors() {
    DelayAdamaStream delay = new DelayAdamaStream(SimpleExecutor.NOW, new NoOpMetricsFactory().makeItemActionMonitor("xyz"));
    for (int k = 0; k < 1000; k++) {
      delay.send("channel", "marker", "message", Callback.DONT_CARE_INTEGER);
    }
    delay.send("channel", "marker", "message", new Callback<Integer>() {
      @Override
      public void success(Integer value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(115917, ex.code);
      }
    });
  }
}
