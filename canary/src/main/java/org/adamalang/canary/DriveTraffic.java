/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.canary;

import org.adamalang.canary.agents.net.LocalNetCanaryConfig;
import org.adamalang.canary.agents.net.LocalNetDrive;
import org.adamalang.canary.agents.simple.SimpleCanaryConfig;
import org.adamalang.canary.agents.simple.SimpleDrive;
import org.adamalang.canary.agents.local.LocalCanaryConfig;
import org.adamalang.canary.agents.local.LocalDrive;
import org.adamalang.common.*;

public class DriveTraffic {
  public static void execute(ConfigObject config) throws Exception {
    String mode = config.strOf("mode", "simple");
    if ("simple".equals(mode)) {
      SimpleDrive.go(new SimpleCanaryConfig(config));
    }
    if ("local".equals(mode)) {
      LocalDrive.go(new LocalCanaryConfig(config));
    }
    if ("localnet".equals(mode)) {
      LocalNetDrive.go(new LocalNetCanaryConfig(config));
    }
  }
}
