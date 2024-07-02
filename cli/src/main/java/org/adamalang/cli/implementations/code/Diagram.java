/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.cli.implementations.code;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.*;

public class Diagram {
  private final StringBuilder mmd;

  public Diagram(String title) {
    this.mmd = new StringBuilder();
    mmd.append("---\n");
    mmd.append("title: ").append(title).append("\n");
    mmd.append("---\n");
    mmd.append("classDiagram\n");
  }

  public String finish() {
    return mmd.toString();
  }

  private String getVisibility(ObjectNode type) {
    try {
      switch (type.get("privacy").textValue()) {
        case "public":
          return "+";
        case "private":
          return "-";
        case "bubble":
          return "#";
        default:
          return "~";
      }
    } catch (Exception ex) {
      System.err.println(type.toPrettyString());
      return "?";
    }
  }

  private String summarizeType(ObjectNode type, Set<String> depends) {
    switch (type.get("nature").textValue()) {
      case "native_value":
      case "reactive_value":
        return type.get("type").textValue();
      case "native_ref":
      case "reactive_ref":
        String ref = type.get("ref").textValue();
        depends.add(ref);
        return ref;
      case "native_list":
      case "reactive_list":
        return "list~" + summarizeType((ObjectNode) type.get("type"), depends) + "~";
      case "native_maybe":
      case "reactive_maybe":
        return "maybe~" + summarizeType((ObjectNode) type.get("type"), depends) + "~";
      case "native_array":
        return "array~" + summarizeType((ObjectNode) type.get("type"), depends) + "~";
      case "native_map":
      case "reactive_map":
        return "map~" + summarizeType((ObjectNode) type.get("domain"), depends) + "~,~" + summarizeType((ObjectNode) type.get("range"), depends) + "~";
      case "reactive_table":
        String tableRecord = type.get("record_name").textValue();
        depends.add(tableRecord);
        return "table~" + tableRecord + "~";
      default:
        System.err.println("unknown:" + type);
        return "?";
    }
  }

  private void fieldsOf(ObjectNode struct, boolean isRecord, Set<String> depends) {
    ObjectNode fields = (ObjectNode) struct.get("fields");
    TreeMap<String, String> sorted = new TreeMap<>();
    Iterator<Map.Entry<String, JsonNode>> it = fields.fields();

    while (it.hasNext()) {
      Map.Entry<String, JsonNode> rec = it.next();
      String name = rec.getKey();
      ObjectNode field = (ObjectNode) rec.getValue();
      if (isRecord) {
        sorted.put(name, getVisibility(field) + summarizeType((ObjectNode) field.get("type"), depends) + " " + name);
      } else {
        sorted.put(name, "+" + summarizeType((ObjectNode) field.get("type"), depends) + " " + name);
      }
    }
    for (Map.Entry<String, String> write : sorted.entrySet()) {
      mmd.append("        ").append(write.getValue()).append("\n");
    }
  }

  private void structsOf(ObjectNode types) {
    Iterator<Map.Entry<String, JsonNode>> it = types.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> rec = it.next();
      String name = rec.getKey();
      ObjectNode type = (ObjectNode) rec.getValue();
      if ("__Root".equals(name)) continue;
      if (name.startsWith("_")) continue;
      TreeSet<String> depends = new TreeSet<>();
      if ("reactive_record".equals(type.get("nature").textValue())) {
        mmd.append("    class ").append(name).append(":::record{\n");
        fieldsOf(type, true, depends);
        mmd.append("    }\n");
      } else if ("native_message".equals(type.get("nature").textValue())) {
        mmd.append("    class ").append(name).append(":::message{\n");
        fieldsOf(type, false, depends);
        mmd.append("    }\n");
      }
      for (String depend : depends) {
        mmd.append("    ").append(name).append("..>").append(depend).append("\n");
      }
    }
  }

  private void linkChannels(ObjectNode channels) {
    Iterator<Map.Entry<String, JsonNode>> it = channels.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> channel = it.next();
      String name = channel.getKey();
      String message = channel.getValue().textValue();
      mmd.append("    class ").append(name).append(":::channel{\n");
      mmd.append("        +").append(message).append(" msg\n");
      mmd.append("    }\n");
      mmd.append("    ").append(name).append("-->Document\n");
      mmd.append("    ").append(message).append("..>").append(name).append("\n");
    }
  }

  public void process(ObjectNode reflection, boolean includeRoot) {
    ObjectNode types = (ObjectNode) reflection.get("types");
    ObjectNode doc = (ObjectNode) types.get("__Root");
    if (includeRoot) {
      mmd.append("    class Document:::document{\n");
      TreeSet<String> depends = new TreeSet<>();
      fieldsOf(doc, true, depends);
      mmd.append("    }\n");
      for (String depend : depends) {
        mmd.append("    Document..>").append(depend).append("\n");
      }
    }
    ObjectNode viewer = (ObjectNode) types.get("__ViewerType");
    mmd.append("    class Viewer{\n");
    fieldsOf(viewer, false, new TreeSet<>());
    mmd.append("    }\n");
    structsOf(types);
    if (includeRoot) {
      mmd.append("    Viewer..>Document\n");
    }
    linkChannels((ObjectNode) reflection.get("channels"));
  }

}
