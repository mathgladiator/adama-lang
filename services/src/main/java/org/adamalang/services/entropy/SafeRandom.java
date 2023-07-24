/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.services.entropy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;

import java.util.HashSet;
import java.util.Random;
import java.util.function.Consumer;

public class SafeRandom extends SimpleService {
  private final Random rng;

  public SafeRandom() {
    super("saferandom", new NtPrincipal("saferandom", "service"), true);
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
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    // NOTE: this is not a "secure" random.
    if ("ask".equals(method)) {
      ObjectNode parsed = Json.parseJsonObject(request);
      String pool = parsed.get("pool").textValue();
      int count = parsed.get("count").intValue();
      StringBuilder sb = new StringBuilder();
      for (int k = 0; k < count; k++) {
        int j = rng.nextInt(pool.length());
        sb.append(pool.charAt(j));
      }
      callback.success("{\"result\":\"" + sb + "\"}");
    }
  }
}
