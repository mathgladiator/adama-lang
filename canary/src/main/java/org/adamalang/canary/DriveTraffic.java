/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.canary;

import org.adamalang.canary.agents.local.LocalCanaryConfig;
import org.adamalang.canary.agents.local.LocalDrive;
import org.adamalang.canary.agents.net.LocalNetCanaryConfig;
import org.adamalang.canary.agents.net.LocalNetDrive;
import org.adamalang.canary.agents.simple.SimpleCanaryConfig;
import org.adamalang.canary.agents.simple.SimpleDrive;
import org.adamalang.common.ConfigObject;

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
