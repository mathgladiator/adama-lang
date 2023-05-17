package org.adamalang.cli.commands;

import org.adamalang.ErrorTable;
import org.adamalang.cli.commands.*;
import org.adamalang.common.ErrorCodeException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.cli.remote.Connection;
import org.adamalang.cli.remote.WebSocketClient;
import org.adamalang.common.Hashing;
import org.adamalang.common.Json;
public class Main { 
  public static void main(String[] preFilteredArgs) throws Exception{
    try {
      Config config = new Config(preFilteredArgs);
      if (preFilteredArgs.length == 0) {
        rootHelp();
        return;
      }
      String[] args = config.argsForTool;
      String command = Util.normalize(args[0]);
      String[] next = Util.tail(args);
      switch (command) {
        case "space":
          Space.execute(config, next);
          return;
        case "help":
          rootHelp();
          return;
      }
    } catch (Exception ex) {
      if (ex instanceof ErrorCodeException) {
        System.err.println(Util.prefix("[ERROR]", Util.ANSI.Red));
        System.err.println("#:" + ((ErrorCodeException) ex).code);
        System.err.println("Name:" + ErrorTable.INSTANCE.names.get(((ErrorCodeException) ex).code));
        System.err.println("Description:" + ErrorTable.INSTANCE.descriptions.get(((ErrorCodeException) ex).code));
      } else {
        ex.printStackTrace();
      }
    }
  }
  public static void rootHelp() {
    System.out.println(Util.prefix("Interacts with the Adama Platform", Util.ANSI.Green));
    System.out.println();
    System.out.println(Util.prefix("USAGE:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("adama", Util.ANSI.Green) + " " + Util.prefix("[SUBCOMMAND]", Util.ANSI.Magenta));
    System.out.println();
    System.out.println(Util.prefix("FLAGS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("--config", Util.ANSI.Green) + "          Supplies a config file path other than the default (~/.adama)");
    System.out.println();
    System.out.println(Util.prefix("SUBCOMMANDS:", Util.ANSI.Yellow));
    System.out.println("    " + Util.prefix("space             ", Util.ANSI.Green) + "Manage a space");
  }
}