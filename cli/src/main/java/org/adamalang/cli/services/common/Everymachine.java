/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.services.common;

import org.adamalang.cli.Config;
import org.adamalang.cli.services.Role;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.jvm.MachineHeat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Everymachine {
  private static final Logger LOGGER = LoggerFactory.getLogger(Everymachine.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOGGER);

  public Everymachine(Config config, Role role) throws Exception {
    MachineHeat.install();
    ConfigObject configObjectForWeb = new ConfigObject(config.get_or_create_child(role == Role.Overlord ? "overlord-web" : "web"));
    if (role == Role.Overlord) {
      configObjectForWeb.intOf("http-port", 8081);
    }
  }
}
