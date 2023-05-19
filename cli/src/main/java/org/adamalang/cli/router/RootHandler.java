package org.adamalang.cli.router;

public abstract class RootHandler {
    SpaceRouter spaceRouter;
    public int route(String[] args) {
        switch (args[0]) {

            case "space":
                return spaceRouter.route(args);
            case "command":
                return command("Arguments", "Output");
            default:

        }
        return 0;
    }


    abstract int command(String Arguments, String Output);


}
