/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.implementations;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.cli.router.AccountHandler;
import org.adamalang.cli.router.Arguments;
import org.adamalang.cli.runtime.Output;
import org.adamalang.common.Json;

public class AccountHandlerImpl implements AccountHandler {
  @Override
  public void setPassword(Arguments.AccountSetPasswordArgs args, Output.YesOrError output) throws Exception {
    Config config = args.config;
    System.out.print("Password:");
    String password = new String(System.console().readPassword());
    String identity = config.get_string("identity", null);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "account/set-password");
        request.put("identity", identity);
        request.put("password", password);
        connection.execute(request);
        output.out();
      }
    }
  }
}
