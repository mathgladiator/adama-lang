package org.adamalang.cli.commands.services.standalone;

import org.adamalang.cli.Config;
import org.adamalang.cli.commands.services.CommonServiceInit;
import org.adamalang.cli.commands.services.distributed.Backend;
import org.adamalang.cli.commands.services.distributed.Frontend;

public class Solo {
  public static void run(Config config) throws Exception {
    // run the core service
    CommonServiceInit init = Backend.run(config);
    // spin up the frontend
    new Frontend(config, init);
  }
}
