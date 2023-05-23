package org.adamalang.cli.router;

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
      default:
        return Help.displayHelp();
    }
  }
  SpaceHandler createSpaceHandler();
  AuthorityHandler createAuthorityHandler();
}