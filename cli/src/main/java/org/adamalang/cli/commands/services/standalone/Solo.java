/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.cli.commands.services.standalone;

import org.adamalang.cli.Config;
import org.adamalang.cli.commands.services.CommonServiceInit;
import org.adamalang.cli.commands.services.distributed.Backend;
import org.adamalang.cli.commands.services.distributed.Frontend;

public class Solo {
  public static void run(Config config) throws Exception {
    // run the core service
    Backend backend = Backend.run(config);
    // spin up the frontend
    new Frontend(config, backend.init, backend.client);
  }
}
