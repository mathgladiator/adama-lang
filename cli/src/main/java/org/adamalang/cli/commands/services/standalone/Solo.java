/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
