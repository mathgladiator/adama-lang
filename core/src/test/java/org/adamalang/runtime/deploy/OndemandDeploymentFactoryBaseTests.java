/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.contracts.PlanFetcher;
import org.adamalang.runtime.data.Key;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class OndemandDeploymentFactoryBaseTests {
  @Test
  public void happy() throws Exception {
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":50,\"prefix\":\"\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase();
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
    ondemand.undeploy("space");
    Assert.assertFalse(ondemand.spacesAvailable().contains("space"));
  }

  @Test
  public void deploy_prior_fetch() throws Exception{
    DeploymentPlan plan = new DeploymentPlan(
        "{\"versions\":{\"x\":\"\",\"y\":\"\",\"z\":\"\"},\"default\":\"z\",\"plan\":[{\"version\":\"x\",\"percent\":0,\"keys\":[\"1\",\"2\"],\"prefix\":\"k\",\"seed\":\"a2\"},{\"version\":\"y\",\"percent\":50,\"prefix\":\"\",\"seed\":\"a2\"}]}",
        (t, errorCode) -> {});

    DeploymentFactoryBase base = new DeploymentFactoryBase();
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

    DeploymentFactoryBase base = new DeploymentFactoryBase();
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

    DeploymentFactoryBase base = new DeploymentFactoryBase();
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
    DeploymentFactoryBase base = new DeploymentFactoryBase();
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
