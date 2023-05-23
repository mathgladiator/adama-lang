package org.adamalang.cli;

import org.adamalang.cli.implementations.RootHandlerImpl;
import org.adamalang.cli.router.RootHandler;

public class NewMain {
    public static void main(String[] args) {
        RootHandler handler = new RootHandlerImpl();
        System.exit(handler.route(args));
    }
}
