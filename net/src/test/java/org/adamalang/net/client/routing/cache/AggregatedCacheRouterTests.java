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
package org.adamalang.net.client.routing.cache;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.net.client.contracts.RoutingCallback;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AggregatedCacheRouterTests {
  @Test
  public void flow() throws Exception {
    AggregatedCacheRouter router = new AggregatedCacheRouter(SimpleExecutor.NOW);
    CountDownLatch latch = new CountDownLatch(7);
    router.random((host) -> {
      Assert.assertNull(host);
      latch.countDown();
    });
    router.list("space", (list) -> {
      Assert.assertEquals(0, list.size());
      latch.countDown();
    });
    router.integrate("t1", Collections.singleton("space"));
    router.random((host) -> {
      Assert.assertEquals("t1", host);
      latch.countDown();
    });
    router.integrate("t2", Collections.singleton("space"));
    router.integrate("t3", Collections.singleton("space"));
    router.get(new Key("space", "key1"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t2", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    router.get(new Key("space", "key2"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t1", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    router.get(new Key("space", "key5"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t3", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    router.remove("t2");
    router.get(new Key("space", "key1"), new RoutingCallback() {
      @Override
      public void onRegion(String region) {

      }

      @Override
      public void onMachine(String machine) {
        Assert.assertEquals("t3", machine);
        latch.countDown();

      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(1000, TimeUnit.MILLISECONDS));
  }
}
