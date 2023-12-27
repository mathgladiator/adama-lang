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
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;

import java.util.HashSet;
import java.util.Random;
import java.util.function.Consumer;

public class Delay extends SimpleService {
  private final FirstPartyMetrics metrics;
  private final SimpleExecutor executor;

  public Delay(FirstPartyMetrics metrics, SimpleExecutor executor) {
    super("delay", new NtPrincipal("delay", "service"), true);
    this.metrics = metrics;
    this.executor = executor;
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _Delay_Request { int count; }\n");
    sb.append("message _Delay_Empty { }\n");
    sb.append("service delay {\n");
    sb.append("  class=\"delay\";\n");
    sb.append("  method<_Delay_Request, _Delay_Empty> milli;\n");
    sb.append("  method<_Delay_Request, _Delay_Empty> ms;\n");
    sb.append("  method<_Delay_Request, _Delay_Empty> sec;\n");
    sb.append("  method<_Delay_Request, _Delay_Empty> seconds;\n");
    sb.append("  method<_Delay_Request, _Delay_Empty> min;\n");
    sb.append("  method<_Delay_Request, _Delay_Empty> minutes;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    int ms = node.get("count").intValue();
    switch (method) {
      case "sec":
      case "seconds":
        ms *= 1000;
        break;
      case "min":
      case "minutes":
        ms *= 60000;
        break;
    }
    executor.schedule(new NamedRunnable("delay") {
      @Override
      public void execute() throws Exception {
        callback.success("{}");
      }
    }, ms);
  }
}
