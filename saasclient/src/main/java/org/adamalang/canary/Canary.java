/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.canary;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.api.ClientConnectionCreateRequest;
import org.adamalang.api.ClientDataResponse;
import org.adamalang.api.SelfClient;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.web.client.WebClientBase;
import org.adamalang.web.client.WebClientBaseMetrics;
import org.adamalang.web.client.socket.ConnectionReady;
import org.adamalang.web.client.socket.MultiWebClientRetryPool;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolConfig;
import org.adamalang.web.client.socket.MultiWebClientRetryPoolMetrics;
import org.adamalang.web.service.WebConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.function.Consumer;

public class Canary {
  public static CanaryEval evalOf(String endpoint, Consumer<String> output) throws Exception {
    SimpleExecutor executorEval = SimpleExecutor.create("pool");
    SimpleExecutor executorRetry = SimpleExecutor.create("pool");
    WebClientBase base = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.newJsonObject())));
    MultiWebClientRetryPoolMetrics metrics = new MultiWebClientRetryPoolMetrics(new NoOpMetricsFactory());
    MultiWebClientRetryPoolConfig config = new MultiWebClientRetryPoolConfig(new ConfigObject(Json.newJsonObject()));
    MultiWebClientRetryPool pool = new MultiWebClientRetryPool(executorRetry, base, metrics, config, ConnectionReady.TRIVIAL, endpoint);
    SelfClient client = new SelfClient(pool);
    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
      @Override
      public void run() {
        executorRetry.shutdown();
        executorEval.shutdown();
        base.shutdown();
      }
    }));
    return new CanaryEval(executorEval, executorRetry, client, new CanaryMetricsRegister(), output);
  }

  public static void run(String endpoint, String scenario, Consumer<String> output) throws Exception {
    File file = new File("canary." + scenario + ".json");
    if (!file.exists()) {
      throw new Exception(file.getName() + " does not exist");
    }
    ObjectNode config = Json.parseJsonObject(Files.readString(file.toPath()));

    CanaryEval eval = evalOf(endpoint, output);
    eval.eval.execute(new NamedRunnable("eval") {
      @Override
      public void execute() throws Exception {
        output.accept("started:" + scenario);
        eval.eval(config);
        output.accept("finished:" + scenario);
      }
    });

    eval.register.poll();
  }


}
