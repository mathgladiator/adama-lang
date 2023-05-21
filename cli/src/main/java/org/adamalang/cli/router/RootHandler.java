package org.adamalang.cli.router;

public interface RootHandler {
  default int route(String[] args) {

    // Should catch all inconsistencies here
    Argument arguments = new Argument(args);
    // Everything from here is validated.

    switch (arguments.group) {
      case "space":
        SpaceRouter spaceRouter = createRouter();
        return spaceRouter.route(arguments);
      default:
        return displayHelp();
    }
  }
  SpaceRouter createRouter();
  default int displayHelp() {
    return 0;
  }
}