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
package org.adamalang.runtime.sys.cron;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockKeyAlarm;
import org.adamalang.runtime.mocks.MockWakeService;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class InMemoryWakeProxyTests {
  @Test
  public void flow() throws Exception {
    SimpleExecutor executor = SimpleExecutor.create("simple");
    MockKeyAlarm mockKeyAlarm = new MockKeyAlarm();
    MockWakeService mockWakeService = new MockWakeService();
    try {
      CountDownLatch latch = new CountDownLatch(2);
      InMemoryWakeProxy proxy = new InMemoryWakeProxy(executor, new KeyAlarm() {
        @Override
        public void wake(Key key) {
          mockKeyAlarm.wake(key);
          latch.countDown();
        }
      }, mockWakeService);
      proxy.wakeIn(new Key("s", "k"), 15, new Callback<Void>() {
        @Override
        public void success(Void value) {
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {}
      });
      Assert.assertTrue(latch.await(50000, TimeUnit.MILLISECONDS));
      Assert.assertEquals("WAKE:s/k@15", mockWakeService.get(0));
      Assert.assertEquals("ALARM:s/k", mockKeyAlarm.get(0));
    } finally {
      executor.shutdown();
    }
  }
}
