package org.adamalang.cli.router;

import org.adamalang.cli.router.ArgumentType.*;

public interface SpaceRouter {
  default int route(Argument args) {
  if (args.command == null) {
    return Help.displayHelp("space");
  }
    switch (args.command.name) {
      case "create":
        CreateSpaceArgs arguments = new CreateSpaceArgs(args);
        return createSpace(arguments, "Output");
      default:
        Help.displayHelp("space");
        return 0;
    }
  }
  int createSpace(CreateSpaceArgs args, String output);
}