/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli;

import org.adamalang.ErrorTable;
import org.adamalang.cli.commands.*;
import org.adamalang.common.ErrorCodeException;

import java.util.Map;

public class Main {

  public static void main(String[] preFilteredArgs) throws Exception {
    try {
      Config config = new Config(preFilteredArgs);
      if (preFilteredArgs.length == 0) {
        NewMain.main(preFilteredArgs);
        return;
      }
      String[] args = config.argsForTool;
      String command = Util.normalize(args[0]);
      String[] next = Util.tail(args);
      switch (command) {
        case "account":
          Account.execute(config, next);
          return;
        case "authority":
          Authority.execute(config, next);
          return;
        case "aws":
          AWS.execute(config, next);
          return;
        case "business":
          Business.execute(config, next);
          return;
        case "database":
          Database.execute(config, next);
          return;
        case "debug":
          Debug.execute(config, next);
          return;
        case "documents":
        case "document":
          Documents.execute(config, next);
          return;
        case "frontend":
          Frontend.execute(config, next);
          return;
        case "init":
          Init.execute(config, next);
          return;
        case "service":
          Service.execute(config, next);
          return;
        case "dumpenv":
          for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            System.err.println(entry.getKey() + "=" + entry.getValue());
          }
          return;
      }
      NewMain.main(args);
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
}
