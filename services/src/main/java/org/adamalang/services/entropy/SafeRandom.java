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
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;

import java.util.HashSet;
import java.util.Random;
import java.util.function.Consumer;

public class SafeRandom extends SimpleService {
  private final Random rng;
  private final SimpleExecutor offload;

  public SafeRandom(SimpleExecutor offload) {
    super("saferandom", new NtPrincipal("saferandom", "service"), true);
    this.rng = new Random();
    this.offload = offload;
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _SafeRandom_AskStr").append(" { string pool; int count; }\n");
    sb.append("message _SafeRandom_Result").append(" { string result; }\n");
    sb.append("service saferandom {\n");
    sb.append("  class=\"saferandom\";\n");
    sb.append("  method<_SafeRandom_AskStr").append(", _SafeRandom_Result").append("> ask;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    offload.execute(new NamedRunnable("randomness") {
      @Override
      public void execute() throws Exception {
        try {
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
          } else {
            callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
          }
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_EXCEPTION));
        }
      }
    });
  }
}
