/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.services.social;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.function.Consumer;

public class Discord extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Discord.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;

  public Discord(FirstPartyMetrics metrics, WebClientBase base) {
    super("discord", new NtPrincipal("discord", "service"), true);
    this.metrics = metrics;
    this.base = base;
  }

  public static Discord build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    return new Discord(metrics, base);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("service discord {\n");
    sb.append("  class=\"discord\";\n");
    sb.append("  ").append(params).append("\n");
    sb.append("  method<dynamic, dynamic> GetMessage;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode requestNode = Json.parseJsonObject(request);
    switch (method) {
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
