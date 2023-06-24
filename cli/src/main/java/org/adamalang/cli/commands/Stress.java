/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.cli.commands;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.canary.DriveTraffic;
import org.adamalang.cli.Config;
import org.adamalang.cli.Util;
import org.adamalang.common.ConfigObject;
import org.adamalang.common.Json;
import java.io.File;
import java.nio.file.Files;

public class Stress {
  public static void execute(Config config, String[] args) throws Exception {
    String scenarioFile = Util.extractOrCrash("--scenario", "-s", args);
    String scenarioJson = Files.readString(new File(scenarioFile).toPath());
    ObjectNode scenarioObject = Json.parseJsonObject(scenarioJson);
    ConfigObject scenario = new ConfigObject(scenarioObject);
    DriveTraffic.execute(scenario);
  }
}
