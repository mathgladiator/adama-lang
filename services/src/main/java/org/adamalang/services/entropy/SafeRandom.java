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
package org.adamalang.services.entropy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.lambdaworks.crypto.SCryptUtil;
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
    sb.append("message _SafeRandom_Empty").append(" { }\n");
    sb.append("message _SafeRandom_AskStr").append(" { string pool; int count; }\n");
    sb.append("message _SafeRandom_Result").append(" { string result; }\n");
    sb.append("message _SafeRandom_ResultWithHash").append(" { string result; string hash; }\n");
    sb.append("service saferandom {\n");
    sb.append("  class=\"saferandom\";\n");
    sb.append("  method<_SafeRandom_AskStr").append(", _SafeRandom_Result").append("> ask;\n");
    sb.append("  method<_SafeRandom_AskStr").append(", _SafeRandom_ResultWithHash").append("> askWithHash;\n");
    sb.append("  method<_SafeRandom_Empty").append(", _SafeRandom_Result").append("> uuid;\n");
    sb.append("  method<_SafeRandom_Empty").append(", _SafeRandom_ResultWithHash").append("> uuidWithHash;\n");
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
          boolean askWithHash = "askWithHash".equals(method);
          boolean uuidWithHash = "uuidWithHash".equals(method);
          if ("ask".equals(method) || askWithHash) {
            ObjectNode parsed = Json.parseJsonObject(request);
            String pool = parsed.get("pool").textValue();
            int count = parsed.get("count").intValue();
            StringBuilder sb = new StringBuilder();
            for (int k = 0; k < count; k++) {
              int j = rng.nextInt(pool.length());
              sb.append(pool.charAt(j));
            }
            String gen = sb.toString();
            ObjectNode result = Json.newJsonObject();
            result.put("result", gen);
            if (askWithHash) {
              result.put("hash", SCryptUtil.scrypt(gen, 16384, 8, 1));
            }
            callback.success(result.toString());
          } else if ("uuid".equals(method) || uuidWithHash) {
            ObjectNode result = Json.newJsonObject();
            String gen = ProtectedUUID.generate();
            result.put("result", gen);
            if (uuidWithHash) {
              result.put("hash", SCryptUtil.scrypt(gen, 16384, 8, 1));
            }
            callback.success(result.toString());
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
