package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Json;

public class Documents {
  public static void execute(Config config, String[] args) throws Exception {
    if (args.length == 0) {
      documentsHelp(args);
      return;
    }
    String command = Util.normalize(args[0]);
    String[] next = Util.tail(args);
    switch (command) {
      case "connect":
        documentsConnect(config, next);
        return;
      case "help":
        documentsHelp(next);
        return;
    }
  }


  private static void documentsConnect(Config config, String[] args) throws Exception {
    String identity = config.get_string("identity", null);
    String space = Util.extractOrCrash("--space", "-s", args);
    String key = Util.extractOrCrash("--key", "-k", args);
    try (WebSocketClient client = new WebSocketClient(config)) {
      try (Connection connection = client.open()) {
        ObjectNode request = Json.newJsonObject();
        request.put("method", "connection/create");
        request.put("identity", identity);
        request.put("space", space);
        request.put("key", key);
        connection.stream(request, (response) -> {
          System.err.println(response.toPrettyString());
        });
      }
    }
  }

  public static void documentsHelp(String[] args) {
    if (args.length > 0) {
      String command = Util.normalize(args[0]);
    }
    System.out.println(Util.prefix("Interact with documents in a limited fashion", Util.ANSI.Green));
    System.out.println("");
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama space", Util.ANSI.Green) + " " + Util.prefix("[SPACESUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println("");
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println("");
    System.out.println(Util.prefix("SPACESUBCOMMAND:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("connect", Util.ANSI.Green) + "            Create a new space");
  }
}
