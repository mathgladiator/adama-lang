package org.adamalang.cli;

import org.adamalang.cli.implementations.RootHandlerImpl;
import org.adamalang.cli.router.RootHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.MainRouter;

public class NewMain {
    public static void main(String[] args) {
        RootHandler handler = new RootHandlerImpl();
        Output output = new Output(args);
        System.exit(MainRouter.route(args, handler, output));
    }

    public static void testMain(String[] args) {
        RootHandler handler = new RootHandlerImpl();
        Output output = new Output(args);
        MainRouter.route(args, handler, output);
    }
}
