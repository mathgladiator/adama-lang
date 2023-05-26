package org.adamalang.cli.router;

import org.adamalang.cli.runtime.Argument;
import org.adamalang.cli.runtime.Help;
import org.adamalang.cli.runtime.Output;

public interface RootHandler {
  default int route(String[] args) {
    Argument arguments = new Argument(args);
    if (!arguments.valid) {
      return 0;
    }
    if (arguments.group == null) {
      return Help.displayHelp();
    }

    switch (arguments.group.name) {
      case "space":
        SpaceHandler spaceHandler = createSpaceHandler();
        return spaceHandler.route(arguments);
      case "authority":
        AuthorityHandler authorityHandler = createAuthorityHandler();
        return authorityHandler.route(arguments);
      case "init":
        return init(new Output(arguments));
      default:
        return Help.displayHelp();
    }
  }
  SpaceHandler createSpaceHandler();
  AuthorityHandler createAuthorityHandler();
  int init(Output output);
}