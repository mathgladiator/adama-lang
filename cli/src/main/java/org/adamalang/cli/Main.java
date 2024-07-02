/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli;

import org.adamalang.cli.router.RootHandler;
import org.adamalang.cli.runtime.Output;
import org.adamalang.cli.router.MainRouter;
import org.adamalang.cli.implementations.RootHandlerImpl;

public class Main {
    public static void main(String[] args) {
        // remove \r
        for (int k = 0; k < args.length; k++) {
            args[k] = args[k].trim();
        }
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
