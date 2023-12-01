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
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.remote.Deliverer;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AsyncCompilerTests {

  private Object pump(DeploymentFactory prior, DeploymentPlan plan) throws Exception {
    return pump(prior, plan, AsyncByteCodeCache.DIRECT);
  }

  public static Object pump(DeploymentFactory prior, DeploymentPlan plan, AsyncByteCodeCache cache) throws Exception {
    AtomicReference<Object> ref = new AtomicReference<>(null);
    CountDownLatch latch = new CountDownLatch(1);
    AsyncCompiler.forge("space", prior, plan, Deliverer.FAILURE, new TreeMap<>(), cache, new Callback<DeploymentFactory>() {
      @Override
      public void success(DeploymentFactory value) {
        ref.set(value);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ref.set(ex);
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    return ref.get();
  }

  @Test
  public void happy_simple() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\"}", (t, errorCode) -> {});
    Assert.assertTrue(pump(null, plan) instanceof DeploymentFactory);
  }

  @Test
  public void happy_simple_instrument() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"instrument\":true,\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\"}", (t, errorCode) -> {});
    Assert.assertTrue(pump(null, plan) instanceof DeploymentFactory);
  }

  @Test
  public void bad_caching() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\"}", (t, errorCode) -> {});
    ErrorCodeException ex = (ErrorCodeException) pump(null, plan, new AsyncByteCodeCache() {
      @Override
      public void fetchOrCompile(String spaceName, String className, String javaSource, String reflection, Callback<CachedByteCode> callback) {
        callback.failure(new ErrorCodeException(12345678));
      }
    });
    Assert.assertEquals(12345678, ex.code);
  }

  @Test
  public void happy_transfer() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\"}", (t, errorCode) -> {});
    Assert.assertTrue(pump((DeploymentFactory) pump(null, plan), plan) instanceof DeploymentFactory);
  }

  @Test
  public void bad_type() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int x = true;\"},\"default\":\"x\"}", (t, errorCode) -> {});
    ErrorCodeException ex = (ErrorCodeException) pump(null, plan);
    Assert.assertEquals(132157, ex.code);
  }

  @Test
  public void bad_parse() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":\"public int;\"},\"default\":\"x\"}", (t, errorCode) -> {});
    ErrorCodeException ex = (ErrorCodeException) pump(null, plan);
    Assert.assertEquals(198174, ex.code);
  }

  @Test
  public void includes_0() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":{\"main\":\"public int x;\",\"includes\":{}}},\"default\":\"x\"}", (t, errorCode) -> {});
    Assert.assertTrue(pump(null, plan) instanceof DeploymentFactory);
  }

  @Test
  public void includes_1() throws Exception {
    DeploymentPlan plan = new DeploymentPlan("{\"versions\":{\"x\":{\"main\":\"public int x; @include x;\",\"includes\":{\"x\":\"public int y = 0;\"}}},\"default\":\"x\"}", (t, errorCode) -> {});
    Assert.assertTrue(pump(null, plan) instanceof DeploymentFactory);
  }
}
