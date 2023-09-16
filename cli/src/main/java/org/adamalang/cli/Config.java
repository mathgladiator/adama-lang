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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Json;
import org.adamalang.runtime.sys.ServiceHeatEstimator;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Config {
  public final String[] argsForTool;
  public final String configPath;
  private ObjectNode cache;

  public Config(String[] args) throws Exception {
    ArrayList<String> argsToUse = new ArrayList<>();
    String _configPath = System.getProperty("user.home") + "/.adama";
    for (int k = 0; k < args.length; k++) {
      if ("--no-color".equals(args[k])) {
        Util.setNoColor();
      } else if ("--config".equals(args[k]) && k + 1 < args.length) {
        _configPath = args[k + 1];
        k++;
      } else {
        argsToUse.add(args[k]);
      }
    }
    this.argsForTool = argsToUse.toArray(new String[argsToUse.size()]);
    this.configPath = _configPath;
    File _configFile = new File(configPath);
    if (!_configFile.exists()) {
      ObjectNode defaultConfig = Json.newJsonObject();
      // TODO: once I have a launch, make this use the default URL and ideal parameters
      Files.writeString(_configFile.toPath(), defaultConfig.toPrettyString());
    }
    this.cache = Json.parseJsonObject(Files.readString(_configFile.toPath()));
  }

  public void manipulate(Consumer<ObjectNode> manipulator) throws Exception {
    File _configFile = new File(configPath);
    ObjectNode config = Json.parseJsonObject(Files.readString(_configFile.toPath()));
    manipulator.accept(config);
    Files.writeString(_configFile.toPath(), config.toPrettyString());
    this.cache = config;
  }

  public static class BadException extends RuntimeException {
    public BadException(String msg) {
      super(msg);
    }
  }

  public String get_string(String field, String defaultValue) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull()) {
      if (defaultValue == null) {
        if ("identity".equals(field)) {
          throw new BadException("The config has no identity; this means you are unable to talk to adama until run: adama init (or java -jar adama.jar init)");
        }
        throw new NullPointerException("expected an '" + field + "' within the config");
      } else {
        return defaultValue;
      }
    }
    return node.textValue();
  }

  public String get_nullable_string(String field) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull()) {
      return null;
    }
    return node.textValue();
  }

  public ObjectNode read() {
    return cache;
  }

  public int get_int(String field, int defaultValue) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull() || !node.isInt()) {
      return defaultValue;
    }
    return node.intValue();
  }

  public boolean get_bool(String field, boolean defaultValue) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull() || !node.isBoolean()) {
      return defaultValue;
    }
    return node.booleanValue();
  }

  public ServiceHeatEstimator.HeatVector get_heat(String suffix, int cpu_m, int messages, int mem_mb, int connections) {
    return new ServiceHeatEstimator.HeatVector(
        get_int(suffix + "-cpu-m", cpu_m) * 1000L * 1000L,
        get_int(suffix + "-messages", messages),
        get_int(suffix + "-mem-mb", mem_mb) * 1024L * 1024,
        get_int(suffix + "-connections", connections));
  }

  public ObjectNode get_or_create_child(String field) {
    JsonNode node = read().get(field);
    if (node instanceof ObjectNode) {
      return (ObjectNode) node;
    }
    return Json.newJsonObject();
  }

  public ArrayList<String> get_str_list(String field) {
    JsonNode node = read().get(field);
    if (node instanceof ArrayNode) {
      ArrayList<String> results = new ArrayList<>(node.size());
      for (int k = 0; k < node.size(); k++) {
        if (node.get(k).isTextual()) {
          results.add(node.get(k).textValue());
        }
      }
      return results;
    }
    return new ArrayList<>();
  }
}
