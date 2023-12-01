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
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.router.DomainHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;

import java.io.File;
import java.nio.file.Files;

public class DomainHandlerImpl implements DomainHandler {
  @Override
  public void list(Arguments.DomainListArgs args, Output.JsonOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "domain/list");
        request.put("identity", identity);
        connection.stream(request, (_k, item) -> {
          output.add(item);
        });
        output.out();
      }
    }
  }

  @Override
  public void configure(Arguments.DomainConfigureArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "domain/configure");
        request.put("identity", identity);
        request.put("domain", args.domain);
        request.put("product-config", Json.parseJsonObject(Files.readString(new File(args.product).toPath())).toString());
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void map(Arguments.DomainMapArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    String autoStr = args.auto.toLowerCase();
    boolean automatic = "true".equals(autoStr) || "yes".equals(autoStr) || args.cert == null;
    final String cert;
    if (!automatic) {
      cert = Files.readString(new File(args.cert).toPath());
    } else {
      cert = null;
    }
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        if (args.key != null) {
          request.put("method", "domain/map-document");
          request.put("key", args.key);
          request.put("route", "true".equals(args.route));
        } else {
          request.put("method", "domain/map");
        }
        request.put("identity", identity);
        request.put("domain", args.domain);
        request.put("space", args.space);
        if (cert != null) {
          request.put("certificate", cert);
        }
        connection.execute(request);
        output.out();
      }
    }
  }

  @Override
  public void unmap(Arguments.DomainUnmapArgs args, Output.YesOrError output) throws Exception {
    String identity = args.config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(args.config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "domain/unmap");
        request.put("identity", identity);
        request.put("domain", args.domain);
        connection.execute(request);
        output.out();
      }
    }
  }
}
