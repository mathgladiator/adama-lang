/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Json;

public class Init {
  public static void execute(Config config, String[] args) throws Exception {
    String email = readEmail(config);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode requestStart = Json.newJsonObject();
        requestStart.put("method", "init/setup-account");
        requestStart.put("email", email);
        connection.execute(requestStart);

        System.out.println();
        System.out.print(Util.prefix("Code:", Util.ANSI.Yellow));
        String code = System.console().readLine();

        ObjectNode requestGenerateIdentity = Json.newJsonObject();
        requestGenerateIdentity.put("method", "init/complete-account");
        requestGenerateIdentity.put("email", email);
        if (revokePrior(config)) {
          requestGenerateIdentity.put("revoke", true);
        }
        requestGenerateIdentity.put("code", code);
        ObjectNode responseGenerateIdentity = connection.execute(requestGenerateIdentity);
        config.manipulate((node) -> {
          node.put("email", email);
          node.set("identity", responseGenerateIdentity.get("identity"));
        });
      }
    }
  }

  private static String readEmail(Config config) {
    System.out.println();
    System.out.print(Util.prefix("Email:", Util.ANSI.Yellow));
    String email = System.console().readLine();
    return email;
  }

  private static boolean revokePrior(Config config) {
    System.out.println();
    System.out.print(Util.prefix("Revoke other machines[Y/n]:", Util.ANSI.Yellow));
    String revokeYesNo = System.console().readLine();
    return revokeYesNo.trim().equalsIgnoreCase("Y");
  }
}
