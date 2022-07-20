/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
