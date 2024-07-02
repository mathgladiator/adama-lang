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
package org.adamalang.services.entropy;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.Json;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;

import java.util.HashSet;
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
    String sb = "message _Delay_Request { int count; int id; }\n" + //
        "message _Delay_Empty { }\n" + //
        "service delay {\n" + //
        "  class=\"delay\";\n" + //
        "  method<_Delay_Request, _Delay_Empty> milli;\n" + //
        "  method<_Delay_Request, _Delay_Empty> ms;\n" + //
        "  method<_Delay_Request, _Delay_Empty> sec;\n" + //
        "  method<_Delay_Request, _Delay_Empty> seconds;\n" + //
        "  method<_Delay_Request, _Delay_Empty> min;\n" + //
        "  method<_Delay_Request, _Delay_Empty> minutes;\n" + //
        "}\n";
    return sb;
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
