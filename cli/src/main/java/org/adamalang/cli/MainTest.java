package org.adamalang.cli;

import org.adamalang.cli.router.*;


public class MainTest {
    public static void main(String[] args) {
        RootHandler handler = new RootHandlerImpl();
        System.exit(handler.route(args));
    }
}
