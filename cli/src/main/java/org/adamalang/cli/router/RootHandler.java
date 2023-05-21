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