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
