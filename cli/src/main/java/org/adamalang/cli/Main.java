/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli;

import org.adamalang.cli.router.RootHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.MainRouter;
import org.adamalang.cli.implementations.RootHandlerImpl;

public class Main {
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
