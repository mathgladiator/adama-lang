package org.adamalang.cli;

import org.adamalang.cli.router.ArgumentType.*;
import org.adamalang.cli.router.RootHandler;
import org.adamalang.cli.router.SpaceRouter;

public class RootHandlerImpl implements RootHandler {

    @Override
    public SpaceRouter createRouter() {
        return new SpaceRouter() {
            @Override
            public int createSpace(CreateSpaceArgs args, String output) {
                System.out.println("Creating space with args " + args.space);
                return 0;
            }
        };
    }

}
