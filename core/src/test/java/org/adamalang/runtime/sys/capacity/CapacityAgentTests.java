/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.capacity;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.LivingDocumentTests;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.deploy.DeploymentFactoryBase;
import org.adamalang.runtime.deploy.Undeploy;
import org.adamalang.runtime.mocks.MockTime;
import org.adamalang.runtime.remote.Deliverer;
import org.adamalang.runtime.sys.CoreMetrics;
import org.adamalang.runtime.sys.CoreService;
import org.adamalang.runtime.sys.ServiceHeatEstimator;
import org.adamalang.runtime.sys.ServiceShield;
import org.adamalang.runtime.sys.mocks.MockInstantDataService;
import org.adamalang.runtime.sys.mocks.MockInstantLivingDocumentFactoryFactory;
import org.adamalang.runtime.sys.mocks.MockMetricsReporter;
import org.adamalang.translator.jvm.LivingDocumentFactory;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class CapacityAgentTests {
  private static final CoreMetrics METRICS = new CoreMetrics(new NoOpMetricsFactory());
  private static final Key KEY = new Key("space", "key");
  private static final String PUBSUB_CODE = "@static {\n" + "  // anyone can create\n" + "  create { return true; }\n" + "}\n" + "\n" + "@connected {\n" + "   // let everyone connect; sure, what can go wrong\n" + "  return true;\n" + "}\n" + "\n" + "// we build a table of publishes with who published it and when they did so\n" + "record Publish {\n" + "  public principal who;\n" + "  public long when;\n" + "  public string payload;\n" + "}\n" + "\n" + "table<Publish> _publishes;\n" + "\n" + "// since tables are private, we expose all publishes to all connected people\n" + "public formula publishes = iterate _publishes order by when asc;\n" + "\n" + "// we wrap a payload inside a message\n" + "message PublishMessage {\n" + "  string payload;\n" + "}\n" + "\n" + "// and then open a channel to accept the publish from any connected client\n" + "channel publish(PublishMessage message) {\n" + "  _publishes <- {who: @who, when: Time.now(), payload: message.payload };\n" + "  \n" + "  // At this point, we encounter a key problem with maintaining a\n" + "  // log of publishes. Namely, the log is potentially infinite, so\n" + "  // we have to leverage some product intelligence to reduce it to\n" + "  // a reasonably finite set which is important for the product.\n" + "\n" + "  // First, we age out publishes too old (sad face)\n" + "  (iterate _publishes\n" + "     where when < Time.now() - 60000L).delete();\n" + "  \n" + "  // Second, we hard cap the publishes biasing younger ones\n" + "  (iterate _publishes\n" + "     order by when desc\n" + "     limit _publishes.size() offset 100).delete();\n" + "     \n" + "  // Hindsight: I should decouple the offset from\n" + "  // the limit because this is currently silly (TODO)\n" + "}";
  private static final String MAXSEQ_CODE = "@static {\n" + "  create { return true; }\n" + "}\n" + "\n" + "@connected {\n" + "  return true;\n" + "}\n" + "\n" + "public int max_db_seq = 0;\n" + "\n" + "message NotifyWrite {\n" + "  int db_seq;\n" + "}\n" + "\n" + "channel notify(NotifyWrite message) {\n" + "  if (message.db_seq > max_db_seq) {\n" + "    max_db_seq = message.db_seq;\n" + "  }\n" + "}";

  @Test
  public void flow() throws Exception {
    MockCapacityOverseer overseer = new MockCapacityOverseer();
    LivingDocumentFactory factory = LivingDocumentTests.compile(PUBSUB_CODE, Deliverer.FAILURE);
    MockInstantLivingDocumentFactoryFactory factoryFactory =
        new MockInstantLivingDocumentFactoryFactory(factory);
    MockTime time = new MockTime();
    MockInstantDataService dataService = new MockInstantDataService();
    CoreService service = new CoreService(METRICS, factoryFactory, (bill) -> {}, new MockMetricsReporter(), dataService, time, 3);
    CapacityMetrics capacityMetrics = new CapacityMetrics(new NoOpMetricsFactory());
    ServiceHeatEstimator.HeatVector low = new ServiceHeatEstimator.HeatVector(1, 1, 1, 100);
    ServiceHeatEstimator.HeatVector high = new ServiceHeatEstimator.HeatVector(1000, 10000, 250, 10000);
    ServiceHeatEstimator estimator = new ServiceHeatEstimator(low, high);
    ServiceShield shield = new ServiceShield();
    String region = "my-region";
    String machine = "my-machine";
    MockUndeploy undeploy = new MockUndeploy();
    try {
      CapacityAgent agent = new CapacityAgent(capacityMetrics, overseer, service, undeploy, estimator, SimpleExecutor.NOW, new AtomicBoolean(false), shield, region, machine);
      agent.addCapacity();
      agent.rebalance();
    } finally {
      service.shutdown();
    }
  }
}
