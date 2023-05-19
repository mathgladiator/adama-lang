package org.adamalang.cli.router;

import org.adamalang.cli.commands.Space;

public class RootHandlerImpl extends RootHandler {

    public SpaceRouter spaceRouter = new SpaceRouter() {
        @Override
        public int createSpace(String argPlaceHolder, String outputPlaceHolder) {
            System.out.println("NICEE!");
            return 0;
        }

        @Override
        public int deleteSpace(String argPlaceHolder, String outputPlaceHolder) {
            return 0;
        }
    };

    @Override
    public int command(String Arguments, String Output) {
        // Implement command here
        return 0;
    }

}
