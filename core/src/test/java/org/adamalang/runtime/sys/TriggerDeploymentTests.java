/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;

public class TriggerDeploymentTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final String PUBSUB_CODE = "@static {\n" + "  // anyone can create\n" + "  create { return true; }\n" + "}\n" + "\n" + "@connected {\n" + "   // let everyone connect; sure, what can go wrong\n" + "  return true;\n" + "}\n" + "\n" + "// we build a table of publishes with who published it and when they did so\n" + "record Publish {\n" + "  public principal who;\n" + "  public long when;\n" + "  public string payload;\n" + "}\n" + "\n" + "table<Publish> _publishes;\n" + "\n" + "// since tables are private, we expose all publishes to all connected people\n" + "public formula publishes = iterate _publishes order by when asc;\n" + "\n" + "// we wrap a payload inside a message\n" + "message PublishMessage {\n" + "  string payload;\n" + "}\n" + "\n" + "// and then open a channel to accept the publish from any connected client\n" + "channel publish(PublishMessage message) {\n" + "  _publishes <- {who: @who, when: Time.now(), payload: message.payload };\n" + "  \n" + "  // At this point, we encounter a key problem with maintaining a\n" + "  // log of publishes. Namely, the log is potentially infinite, so\n" + "  // we have to leverage some product intelligence to reduce it to\n" + "  // a reasonably finite set which is important for the product.\n" + "\n" + "  // First, we age out publishes too old (sad face)\n" + "  (iterate _publishes\n" + "     where when < Time.now() - 60000L).delete();\n" + "  \n" + "  // Second, we hard cap the publishes biasing younger ones\n" + "  (iterate _publishes\n" + "     order by when desc\n" + "     limit _publishes.size() offset 100).delete();\n" + "     \n" + "  // Hindsight: I should decouple the offset from\n" + "  // the limit because this is currently silly (TODO)\n" + "}";

  @Test
  public void coverage() throws Exception {
    LivingDocumentFactory factory = LivingDocumentTests.compile(PUBSUB_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory = new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {
    }, dataService, time, 3);
    TriggerDeployment td = new TriggerDeployment(service, Callback.DONT_CARE_VOID);
    td.success(null);
    td.finished(100);
    td.bumpDocument(true);
    td.bumpDocument(false);
    td.failure(new ErrorCodeException(-123));
    td.witnessException(new ErrorCodeException(123));
  }
}
