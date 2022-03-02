package org.adamalang.canary;

import org.adamalang.canary.agents.simple.SimpleCanaryConfig;
import org.adamalang.canary.agents.simple.SimpleDrive;
import org.adamalang.canary.local.LocalCanaryConfig;
import org.adamalang.canary.local.LocalDrive;
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
  }
}
