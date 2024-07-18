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
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.sys.PredictiveInventory;
import org.adamalang.translator.env.RuntimeEnvironment;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OndemandDeploymentFactoryBaseTests {
  @Test
  public void happy() throws Exception {
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":50,\"prefix\":\"\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    Assert.assertFalse(base.contains("space"));
    MockDeploySync sync = new MockDeploySync();
    OndemandDeploymentFactoryBase ondemand = new OndemandDeploymentFactoryBase(new DeploymentMetrics(new NoOpMetricsFactory()), base, new PlanFetcher() {
      @Override
      public void find(String space, Callback<DeploymentBundle> callback) {
        Assert.assertEquals("space", space);
        callback.success(new DeploymentBundle(plan, new TreeMap<>()));
      }
    }, sync);
    CountDownLatch latch = new CountDownLatch(2);
    ondemand.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    ondemand.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(base.contains("space"));
    sync.assertContains("space");
    CountDownLatch happyDeploy = new CountDownLatch(1);
    ondemand.deploy("space", new Callback<Void>() {
      @Override
      public void success(Void value) {
        happyDeploy.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(happyDeploy.await(10000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(ondemand.spacesAvailable().contains("space"));

    HashMap<String, PredictiveInventory.MeteringSample> account = new HashMap<>();
    ondemand.account(account);
    Assert.assertNotNull(account.get("space"));
    Assert.assertEquals(272970, account.get("space").memory);
    ondemand.account(account);
    Assert.assertEquals(545940, account.get("space").memory);
    ondemand.undeploy("space");
    Assert.assertFalse(ondemand.spacesAvailable().contains("space"));
    account = new HashMap<>();
    ondemand.account(account);
    Assert.assertNull(account.get("space"));
  }

  @Test
  public void deploy_prior_fetch() throws Exception{
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":50,\"prefix\":\"\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    Assert.assertFalse(base.contains("space"));
    MockDeploySync sync = new MockDeploySync();
    OndemandDeploymentFactoryBase ondemand = new OndemandDeploymentFactoryBase(new DeploymentMetrics(new NoOpMetricsFactory()), base, new PlanFetcher() {
      @Override
      public void find(String space, Callback<DeploymentBundle> callback) {
        Assert.assertEquals("space", space);
        callback.success(new DeploymentBundle(plan, new TreeMap<>()));
      }
    }, sync);
    CountDownLatch happyDeploy = new CountDownLatch(1);
    ondemand.deploy("space", new Callback<Void>() {
      @Override
      public void success(Void value) {
        happyDeploy.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(happyDeploy.await(10000, TimeUnit.MILLISECONDS));
    CountDownLatch latch = new CountDownLatch(2);
    ondemand.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    ondemand.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    Assert.assertTrue(base.contains("space"));
    sync.assertContains("space");
  }

  @Test
  public void bad_deploy() throws Exception {
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"int x\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":50,\"prefix\":\"\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    Assert.assertFalse(base.contains("space"));
    MockDeploySync sync = new MockDeploySync();
    OndemandDeploymentFactoryBase ondemand = new OndemandDeploymentFactoryBase(new DeploymentMetrics(new NoOpMetricsFactory()), base, new PlanFetcher() {
      @Override
      public void find(String space, Callback<DeploymentBundle> callback) {
        Assert.assertEquals("space", space);
        callback.success(new DeploymentBundle(plan, new TreeMap<>()));
      }
    }, sync);
    CountDownLatch sadDeploy = new CountDownLatch(1);
    ondemand.deploy("space", new Callback<Void>() {
      @Override
      public void success(Void value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        sadDeploy.countDown();
      }
    });
    Assert.assertTrue(sadDeploy.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void sad() throws Exception {
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"int x\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":50,\"prefix\":\"\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    Assert.assertFalse(base.contains("space"));
    MockDeploySync sync = new MockDeploySync();
    OndemandDeploymentFactoryBase ondemand = new OndemandDeploymentFactoryBase(new DeploymentMetrics(new NoOpMetricsFactory()), base, new PlanFetcher() {
      @Override
      public void find(String space, Callback<DeploymentBundle> callback) {
        Assert.assertEquals("space", space);
        callback.success(new DeploymentBundle(plan, new TreeMap<>()));
      }
    }, sync);
    CountDownLatch latch = new CountDownLatch(1);
    ondemand.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    Assert.assertFalse(base.contains("space"));
    sync.assertDoesNotContains("space");
  }

  @Test
  public void cant_find() throws Exception {
    DeploymentFactoryBase base = new DeploymentFactoryBase(AsyncByteCodeCache.DIRECT, RuntimeEnvironment.Tooling);
    Assert.assertFalse(base.contains("space"));
    MockDeploySync sync = new MockDeploySync();
    OndemandDeploymentFactoryBase ondemand = new OndemandDeploymentFactoryBase(new DeploymentMetrics(new NoOpMetricsFactory()), base, new PlanFetcher() {
      @Override
      public void find(String space, Callback<DeploymentBundle> callback) {
        callback.failure(new ErrorCodeException(123));
      }
    }, sync);

    CountDownLatch latch = new CountDownLatch(1);
    ondemand.fetch(new Key("space", "key"), new Callback<LivingDocumentFactory>() {
      @Override
      public void success(LivingDocumentFactory value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
        latch.countDown();
      }
    });
    Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    CountDownLatch sadDeploy = new CountDownLatch(1);
    ondemand.deploy("space", new Callback<Void>() {
      @Override
      public void success(Void value) {

      }

      @Override
      public void failure(ErrorCodeException ex) {
        sadDeploy.countDown();
      }
    });
    Assert.assertTrue(sadDeploy.await(10000, TimeUnit.MILLISECONDS));
  }
}
