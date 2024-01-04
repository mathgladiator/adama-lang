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
package org.adamalang.cli.implementations.space;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.implementations.CodeHandlerImpl;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;
import org.adamalang.validators.ValidatePlan;

import java.io.File;
import java.nio.file.Files;

/** the tool to deploy */
public class Deployer {
  public static void deploy(Arguments.SpaceDeployArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    final String planJson;
    if (args.file == null && args.plan == null) {
      throw new Exception("require either --file or --plan");
    }
    final String filename;
    if (args.file != null) {
      filename = args.file;
      String singleScript = Files.readString(new File(args.file).toPath());
      ObjectNode plan = Json.newJsonObject();
      ObjectNode version = plan.putObject("versions").putObject("file");
      version.put("main", singleScript);
      plan.put("default", "file");
      plan.putArray("plan");
      planJson = plan.toString();
    } else {
      filename = args.plan;
      planJson = Files.readString(new File(args.plan).toPath());
    }
    if (!CodeHandlerImpl.sharedValidatePlan(planJson, filename)) {
      throw new Exception("Failed to validate plan");
    }
    ValidatePlan.validate(args.space, Json.parseJsonObject(planJson));
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "space/set");
        request.put("identity", identity);
        request.put("space", args.space);
        request.set("plan", Json.parseJsonObject(planJson));
        connection.execute(request);
        output.out();
      }
    }
  }
}
