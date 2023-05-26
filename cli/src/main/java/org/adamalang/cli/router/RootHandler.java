package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Argument;
import org.adamalang.cli.runtime.Help;
import org.adamalang.cli.runtime.Output;
import org.adamalang.ErrorTable;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.cli.Util;
import org.adamalang.cli.router.ArgumentType.*;

public interface RootHandler {
  default int route(String[] args) {
    Argument arguments = new Argument(args);
    if (!arguments.valid) {
      return 0;
    }
    if (arguments.group == null) {
      return Help.displayHelp();
    }

    try {
      switch (arguments.group.name) {
        case "space":
          SpaceHandler spaceHandler = createSpaceHandler();
          return spaceHandler.route(arguments);
        case "authority":
          AuthorityHandler authorityHandler = createAuthorityHandler();
          return authorityHandler.route(arguments);
        case "init":
          return init(new InitArgs(arguments), new Output(arguments));
        default:
          return Help.displayHelp();
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
      return 1;
    }
  }
  SpaceHandler createSpaceHandler();
  AuthorityHandler createAuthorityHandler();
  int init(InitArgs args, Output output) throws Exception;
}