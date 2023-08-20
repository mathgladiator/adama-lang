/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.mysql.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;

import org.adamalang.mysql.*;
import org.adamalang.mysql.model.Hosts;
import org.adamalang.runtime.sys.capacity.CapacityInstance;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class GlobalCapacityOverseerTests {
  @Test
  public void flow() throws Exception {
    DataBaseConfig dataBaseConfig = DataBaseConfigTests.getLocalIntegrationConfig();
    try (DataBase dataBase = new DataBase(dataBaseConfig, new DataBaseMetrics(new NoOpMetricsFactory()))) {
      Installer installer = new Installer(dataBase);
      try {
        installer.install();
        GlobalCapacityOverseer overseer = new GlobalCapacityOverseer(dataBase);

        CountDownLatch latchAdd = new CountDownLatch(4);
        overseer.add("space", "region-1", "machine-a", success(latchAdd));
        overseer.add("space", "region-1", "machine-b", success(latchAdd));
        overseer.add("space-two", "region-1", "machine-a", success(latchAdd));
        overseer.add("space", "region-2", "machine-c", success(latchAdd));
        Assert.assertTrue(latchAdd.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch remove = new CountDownLatch(1);
        overseer.remove("space", "region-1", "machine-b", success(remove));
        Assert.assertTrue(remove.await(5000, TimeUnit.MILLISECONDS));

        CountDownLatch summarize = new CountDownLatch(3);
        overseer.listAllSpace("space", new Callback<List<CapacityInstance>>() {
          @Override
          public void success(List<CapacityInstance> value) {
            Assert.assertEquals(2, value.size());
            summarize.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });

        overseer.listAllOnMachine("region-1", "machine-a", new Callback<List<CapacityInstance>>() {
          @Override
          public void success(List<CapacityInstance> value) {
            Assert.assertEquals(2, value.size());
            summarize.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });

        overseer.listWithinRegion("space", "region-2", new Callback<List<CapacityInstance>>() {
          @Override
          public void success(List<CapacityInstance> value) {
            Assert.assertEquals(1, value.size());
            summarize.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });


        Assert.assertTrue(summarize.await(50000, TimeUnit.MILLISECONDS));

        CountDownLatch nuked = new CountDownLatch(1);
        overseer.nuke("space", success(nuked));
        Assert.assertTrue(nuked.await(5000, TimeUnit.MILLISECONDS));

        Hosts.initializeHost(dataBase, "region-a", "machine-1", "adama", "public-key");
        Hosts.initializeHost(dataBase, "region-a", "machine-2", "adama", "public-key");
        Hosts.initializeHost(dataBase, "region-a", "machine-3", "adama", "public-key");
        Hosts.initializeHost(dataBase, "region-a", "machine-4", "adama", "public-key");

        CountDownLatch picked = new CountDownLatch(2);
        overseer.pickStableHostForSpace("picker-space", "region-a", new Callback<String>() {
          @Override
          public void success(String value) {
            Assert.assertEquals("machine-3", value);
            picked.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {

          }
        });

        overseer.pickStableHostForSpace("the-picker", "region-a", new Callback<String>() {
          @Override
          public void success(String value) {
            Assert.assertEquals("machine-1", value);
            picked.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
        Assert.assertTrue(picked.await(5000, TimeUnit.MILLISECONDS));
        CountDownLatch next = new CountDownLatch(1);
        overseer.add("picker-space", "region-a", "machine-3", success(next));
        Assert.assertTrue(next.await(10000, TimeUnit.MILLISECONDS));

        nextHost(overseer, "machine-4");
        nextHost(overseer, "machine-2");
        nextHost(overseer, "machine-1");

        CountDownLatch failures = new CountDownLatch(2);
        overseer.pickNewHostForSpace("picker-space", "region-a", new Callback<String>() {
          @Override
          public void success(String value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            failures.countDown();
          }
        });

        overseer.pickStableHostForSpace("some-space", "region-none", new Callback<String>() {
          @Override
          public void success(String value) {

          }

          @Override
          public void failure(ErrorCodeException ex) {
            failures.countDown();
          }
        });

        Assert.assertTrue(failures.await(10000, TimeUnit.MILLISECONDS));
      } finally {
        installer.uninstall();
      }
    }
  }

  private void nextHost(GlobalCapacityOverseer overseer, String expected) throws Exception {

    CountDownLatch nextValues = new CountDownLatch(1);
    overseer.pickNewHostForSpace("picker-space", "region-a", new Callback<String>() {
      @Override
      public void success(String machine) {
        Assert.assertEquals(expected, machine);
        overseer.add("picker-space", "region-a", machine, new Callback<Void>() {
          @Override
          public void success(Void value) {
            nextValues.countDown();
          }

          @Override
          public void failure(ErrorCodeException ex) {
            ex.printStackTrace();
          }
        });
      }

      @Override
      public void failure(ErrorCodeException ex) {
        ex.printStackTrace();
      }
    });
    Assert.assertTrue(nextValues.await(5000, TimeUnit.MILLISECONDS));

  }

  private static <T> Callback<T> success(CountDownLatch latch) {
    return new Callback<T>() {
      @Override
      public void success(T value) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    };
  }
}
