/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.standalone;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.distributed.Backend;
import org.adamalang.cli.services.distributed.Frontend;
import org.adamalang.net.client.LocalRegionClient;
import org.adamalang.runtime.sys.capacity.HeatMonitor;

public class Solo {
  public static void run(Config config) throws Exception {
    // run the core service
    Backend backend = Backend.run(config);

    LocalRegionClient client = backend.init.makeLocalClient(new HeatMonitor() {
      @Override
      public void heat(String machine, double cpu, double memory) {

      }
    });
    // spin up the frontend
    new Frontend(config, backend.init, client);
  }
}
