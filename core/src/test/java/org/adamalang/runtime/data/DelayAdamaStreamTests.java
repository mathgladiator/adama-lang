/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
    delay.password("p", Callback.DONT_CARE_INTEGER);
    delay.close();
    MockAdamaStream stream = new MockAdamaStream();
    delay.ready(stream);
    delay.unready();
    Assert.assertEquals("ATTACH:id/name/type/1000/md5/sha\n" + "CANATTACH\n" + "UPDATE:UPDATE!\n" + "SEND:channel/marker/message\n" + "SEND:failure/marker/message\n" + "PASSWORD!\n" + "CLOSE\n", stream.toString());
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
