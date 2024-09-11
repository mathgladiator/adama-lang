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
package org.adamalang.runtime.sys.readonly;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.DataObserver;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.mocks.SimpleVoidCallback;
import org.adamalang.runtime.mocks.MockBackupService;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.mocks.MockWakeService;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockMetricsReporter;
import org.adamalang.runtime.sys.mocks.MockReplicationInitiator;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class LocalReplicationProxyTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE =
      "@static { create { return true; } } public int x = 100;";

  @Test
  public void skip() throws Exception {
    MockReplicationInitiator initiator = new MockReplicationInitiator();
    LocalReplicationProxy proxy = new LocalReplicationProxy("me", initiator);
    AtomicReference<Runnable> runGot = new AtomicReference<>(null);
    CountDownLatch latch = new CountDownLatch(3);
    proxy.startDocumentReplication(KEY, new DataObserver() {
      @Override
      public String machine() {
        return null;
      }

      @Override
      public void start(String snapshot) {
        latch.countDown();
      }

      @Override
      public void change(String delta) {
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException exception) {

      }
    }, new Callback<Runnable>() {
      @Override
      public void success(Runnable value) {
        runGot.set(value);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
  }

  @Test
  public void go_to_service() throws Exception {
    MockReplicationInitiator initiator = new MockReplicationInitiator();
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {},  new MockMetricsReporter(), dataService, new MockBackupService(), new MockWakeService(), new MockReplicationInitiator(), time, 3);
    LocalReplicationProxy proxy = new LocalReplicationProxy("me", initiator);
    proxy.initialize(service);

    SimpleVoidCallback started = new SimpleVoidCallback();
    service.create(ContextSupport.WRAP(new NtPrincipal("agent", "doom")), KEY, "{}", "123", started);
    started.assertSuccess();
    CountDownLatch latch = new CountDownLatch(2);
    AtomicReference<Runnable> cancel = new AtomicReference<>(null);
    proxy.startDocumentReplication(KEY, new DataObserver() {
      @Override
      public String machine() {
        return "me";
      }

      @Override
      public void start(String snapshot) {
        Assert.assertEquals(100, Json.parseJsonObject(snapshot).get("x").asInt());
        latch.countDown();
      }

      @Override
      public void change(String delta) {
      }

      @Override
      public void failure(ErrorCodeException exception) {

      }
    }, new Callback<Runnable>() {
      @Override
      public void success(Runnable value) {
        cancel.set(value);
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {

      }
    });
    Assert.assertTrue(latch.await(10000, TimeUnit.MILLISECONDS));
    Assert.assertNotNull(cancel.get());
    cancel.get().run();
  }
}
