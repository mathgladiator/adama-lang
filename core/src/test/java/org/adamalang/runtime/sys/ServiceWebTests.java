package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.TimeSource;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.ContextSupport;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.NullCallbackLatch;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebResponse;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ServiceWebTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String SIMPLE_CODE_MSG = "@static { create { return true; } } @web get / {\n" + "  return {html:\"root\"};\n" + "}\n" + "\n" + "@web get /fixed {\n" + "  return {html:\"fixed path\"};\n" + "}\n" + "\n" + "@web get /path0/$x:int {\n" + "  return {html:\"path integer:\" + x};\n" + "}\n" + "\n" + "@web get /path1/$x:double {\n" + "  return {html:\"path double:\" + x};\n" + "}\n" + "\n" + "@web get /path2/$x:long {\n" + "  return {html:\"path long without child:\" + x};\n" + "}\n" + "\n" + "@web get /path2/$x:long/child {\n" + "  return {html:\"path long with child: \" + x + \"!\"};\n" + "}\n" + "\n" + "@web get /path3/$a* {\n" + "  return {html:\"tail:\" + a};\n" + "}\n" + "\n" + "@web get /path3/$a:string/child {\n" + "  return {html:\"abort tail and go with direct child:\" + a};\n" + "}";

  @Test
  public void web_basic() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(SIMPLE_CODE_MSG);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    TimeSource time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, dataService, time, 3);
    try {
      NullCallbackLatch created = new NullCallbackLatch();
      service.create(ContextSupport.WRAP(NtClient.NO_ONE), KEY, "{}", null, created);
      created.await_success();

      CountDownLatch latch = new CountDownLatch(3);
      service.webGet(KEY, new WebGet(NtClient.NO_ONE, "/", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
        @Override
        public void success(WebResponse value) {
          Assert.assertEquals("root", value.body);
          latch.countDown();
        }

        @Override
        public void failure(ErrorCodeException ex) {

        }
      });
      service.webGet(KEY, new WebGet(NtClient.NO_ONE, "/nope", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
        @Override
        public void success(WebResponse value) {

        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(133308, ex.code);
          latch.countDown();
        }
      });
      service.webGet(new Key("nope", "noep"), new WebGet(NtClient.NO_ONE, "/", new TreeMap<>(), new NtDynamic("{}")), new Callback<>() {
        @Override
        public void success(WebResponse value) {
        }

        @Override
        public void failure(ErrorCodeException ex) {
          Assert.assertEquals(625676, ex.code);
          latch.countDown();
        }
      });
      Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    } finally {
      service.shutdown();
    }
  }
}
