/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
    boolean _requestingHelp = false;
    for (int k = 0; k < args.length; k++) {
      if ("--config".equals(args[k]) && k + 1 < args.length) {
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

  public String get_string(String field, String defaultValue) {
    JsonNode node = read().get(field);
    if (node == null || node.isNull()) {
      if (defaultValue == null) {
        throw new NullPointerException("expected an '" + field + "' within the config");
      } else {
        return defaultValue;
      }
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
