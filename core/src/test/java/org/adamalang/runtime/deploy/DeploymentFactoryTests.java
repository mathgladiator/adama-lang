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
import java.util.concurrent.atomic.AtomicInteger;

public class DeploymentFactoryTests {

  @Test
  public void cantParse() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"@con\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT);
    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(117823, ex.code);
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void cantType() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = true;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT);

    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        Assert.fail();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.assertEquals(132157, ex.code);
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void happy() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT);
    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    Assert.assertEquals("0w9NHaDbD2fTGSLlHuGyCQ==", base.hashOf("space"));
  }

  @Test
  public void rxhtml() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":{\"main\":\"public int x = 123;\",\"rxhtml\":\"<forest><page uri=\\\"/\\\">Hello World</page></forest>\"}},\"default\":\"x\",\"plan\":[]}",
            (t, errorCode) -> {});
    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT);
    CountDownLatch latch = new CountDownLatch(1);
    base.deploy("space", plan, new TreeMap<>(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        Assert.fail();
      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    Assert.assertEquals("W9ngBjBRMNDRTZY3N3OjKA==", base.hashOf("space"));
  }

  @Test
  public void happyDirect() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactory newFactory = SyncCompiler.forge("space", "Space_", new AtomicInteger(1000), null, plan, Deliverer.FAILURE, new TreeMap<>());
    Assert.assertEquals(1, newFactory.spacesAvailable().size());
  }

  @Test
  public void happyInstrument() throws Exception {
    DeploymentPlan plan =
        new DeploymentPlan(
            "{\"instrument\":true,\"versions\":{\"x\":\"public int x = 123;\"},\"default\":\"x\",\"plan\":[{\"version\":\"x\",\"percent\":50,\"prefix\":\"k\",\"seed\":\"a2\"}]}",
            (t, errorCode) -> {});
    DeploymentFactory newFactory = SyncCompiler.forge("space", "Space_", new AtomicInteger(1000), null, plan, Deliverer.FAILURE, new TreeMap<>());
    Assert.assertEquals(1, newFactory.spacesAvailable().size());
  }
}
