package org.adamalang.services.entropy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SafeRandom extends SimpleService {
  private final ExecutorService executor;
  private final Random rng;

  public SafeRandom(ExecutorService executor) {
    super("saferandom", new NtPrincipal("saferandom", "service"), true);
    this.executor = executor;
    this.rng = new Random();
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message SafeRandom_AskStr_").append(uniqueId).append(" { string pool; int count; }\n");
    sb.append("message SafeRandom_Result_").append(uniqueId).append(" { string result; }\n");
    sb.append("service saferandom {\n");
    sb.append("  class=\"saferandom\";\n");
    sb.append("  method<SafeRandom_AskStr_").append(uniqueId).append(", SafeRandom_Result_").append(uniqueId).append("> ask;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(String method, String request, Callback<String> callback) {
    if ("ask".equals(method)) {
      ObjectNode parsed = Json.parseJsonObject(request);
      String pool = parsed.get("pool").textValue();
      int count = parsed.get("count").intValue();
      String result = pool + count;
      callback.success("{\"result\":\"" + result + "\"}");
    }
  }
}
