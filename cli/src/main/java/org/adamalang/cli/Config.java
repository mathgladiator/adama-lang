/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.cli;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.common.ColorUtilTools;
import org.adamalang.system.contracts.JsonConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Config extends JsonConfig {
  public final String[] argsForTool;
  public final String configPath;

  public Config(ObjectNode cache, String[] argsForTool, String configPath) throws Exception {
    super(cache);
    this.argsForTool = argsForTool;
    this.configPath = configPath;
  }

  public void manipulate(Consumer<ObjectNode> manipulator) throws Exception {
    File _configFile = new File(configPath);
    ObjectNode config = Json.parseJsonObject(Files.readString(_configFile.toPath()));
    manipulator.accept(config);
    Files.writeString(_configFile.toPath(), config.toPrettyString());
    this.cache = config;
  }

  public String get_nullable_string(String field) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull()) {
      return null;
    }
    return node.textValue();
  }

  public static Config fromArgs(String[] args) throws Exception {
    ArrayList<String> argsToUse = new ArrayList<>();
    String _configPath = System.getProperty("user.home") + "/.adama";
    for (int k = 0; k < args.length; k++) {
      if ("--no-color".equals(args[k])) {
        ColorUtilTools.setNoColor();
      } else if ("--config".equals(args[k]) && k + 1 < args.length) {
        _configPath = args[k + 1];
        k++;
      } else {
        argsToUse.add(args[k]);
      }
    }
    File _configFile = new File(_configPath);
    if (!_configFile.exists()) {
      ObjectNode defaultConfig = Json.newJsonObject();
      Files.writeString(_configFile.toPath(), defaultConfig.toPrettyString());
    }
    ObjectNode cache = Json.parseJsonObject(Files.readString(_configFile.toPath()));
    return new Config(cache, argsToUse.toArray(new String[argsToUse.size()]), _configPath);
  }

  public String getMasterKey() throws Exception {
    File masterKeyFile = new File("master.key.json");
    if (!masterKeyFile.exists()) {
      throw new Exception(masterKeyFile.getName() + " doesn't exist, it must");
    }
    return Json.parseJsonObject(Files.readString(masterKeyFile.toPath())).get("mk").textValue();
  }
}
