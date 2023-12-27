/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.services.logging;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.web.client.*;
import org.adamalang.web.service.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Logzio extends SimpleService {
  private static final Logger LOG = LoggerFactory.getLogger(Logzio.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final SimpleExecutor offload;
  private final String token;
  private final String logType;

  public Logzio(FirstPartyMetrics metrics, WebClientBase base, SimpleExecutor offload, String token, String logType) {
    super("logzio", new NtPrincipal("logzio", "service"), true);
    this.metrics = metrics;
    this.base = base;
    this.offload = offload;
    this.token = token;
    this.logType = logType;
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _LogEmptyResult { }\n");
    sb.append("service logzio {\n");
    sb.append("  class=\"logzio\";\n");
    if (!names.contains("token")) {
      error.accept("token is required and it should be encrypted");
    }
    sb.append("  ").append(params).append("\n");
    sb.append("  method<dynamic, _LogEmptyResult> log;\n");
    sb.append("}\n");
    return sb.toString();
  }

  public static Logzio build(FirstPartyMetrics metrics, WebClientBase base, ServiceConfig config, SimpleExecutor offload) throws ErrorCodeException {
    return new Logzio(metrics, base, offload, config.getDecryptedSecret("token"), config.getString("log_type", ""));
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    switch (method) {
      case "log": {
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
      }
      break;
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }

  public static void main(String[] args) throws Exception {
    WebClientBase base = new WebClientBase(new WebClientBaseMetrics(new NoOpMetricsFactory()), new WebConfig(new ConfigObject(Json.newJsonObject())));
    String token = "<<THE TOKEN>>";
    ObjectNode event = Json.newJsonObject();
    event.put("@timestamp", LogTimestamp.now());
    event.put("ag", "jeff");
    event.put("au", "foo");
    event.put("message", "the token was created");

    StringBuilder sb = new StringBuilder();
    sb.append(event.toString()).append("\n");
    SimpleHttpRequest request = new SimpleHttpRequest("POST", "https://listener.logz.io:8071/?token=" + token, new TreeMap<>(), SimpleHttpRequestBody.WRAP(sb.toString().getBytes()));
    CountDownLatch latch = new CountDownLatch(1);
    base.executeShared(request, new VoidCallbackHttpResponder(LOG, new NoOpMetricsFactory().makeRequestResponseMonitor("x").start(), new Callback<Void>() {
      @Override
      public void success(Void value) {
        System.err.println("TRUE");
        latch.countDown();
      }

      @Override
      public void failure(ErrorCodeException ex) {
        System.err.println("EX:" + ex.code);
        latch.countDown();
      }
    }));
    latch.await(1000, TimeUnit.MILLISECONDS);
  }
}
