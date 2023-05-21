package org.adamalang.cli.router;

public interface SpaceRouter {
  default int route(Argument args) {
    switch (args.command) {
      case "create":
        return createSpace("BRUH", "BRUH");
      default:
        Help.displayHelp("space");
        return 0;
    }
  }
  int createSpace(String arguments, String output);
}