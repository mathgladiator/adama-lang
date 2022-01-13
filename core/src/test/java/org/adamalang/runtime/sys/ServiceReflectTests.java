package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class ServiceReflectTests {
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG =
      "@can_create(who) { return true; } public int x; @connected(who) { x = 42; return who == @no_one; } message M {} channel foo(M y) { x += 100; }";

  @Test
  public void reflect_happy() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference valueRef = new AtomicReference(null);
      service.reflect(KEY, new Callback<String>() {
        @Override
        public void success(String value) {
          valueRef.set(value);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
      Assert.assertEquals(
          "{\"types\":{\"#root\":{\"nature\":\"reactive_record\",\"name\":\"Root\",\"fields\":{\"x\":{\"type\":{\"nature\":\"reactive_value\",\"type\":\"int\"},\"privacy\":\"public\"}}},\"__ViewerType\":{\"nature\":\"native_message\",\"name\":\"__ViewerType\",\"anonymous\":true,\"fields\":{}},\"M\":{\"nature\":\"native_message\",\"name\":\"M\",\"anonymous\":false,\"fields\":{}}},\"channels\":{\"foo\":\"M\"},\"constructors\":[],\"labels\":[]}",
          valueRef.get());
    } finally {
      service.shutdown();
    }
  }


  @Test
  public void reflect_sad() throws Exception {
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(null);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      CountDownLatch latch = new CountDownLatch(1);
      AtomicReference valueRef = new AtomicReference(null);
      service.reflect(KEY, new Callback<String>() {
        @Override
        public void success(String value) {
        }

        @Override
        public void failure(ErrorCodeException ex) {
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(2500, TimeUnit.MILLISECONDS));
    } finally {
      service.shutdown();
    }
  }
}
