/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml.phases.generate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** a list of fields that are indexed by path */
public class FieldList implements Iterable<Field> {
  private static final Logger LOG = LoggerFactory.getLogger(FieldList.class);
  private final ArrayList<Field> fields;
  private final HashMap<String, Field> fieldsIndexed;

  public FieldList(ArrayList<Field> fields) {
    this.fields = fields;
    this.fieldsIndexed = new HashMap<>();
    for (Field f : fields) {
      fieldsIndexed.put(f.name, f);
    }
  }

  public static FieldList of(ObjectNode type, ObjectNode root) {
    ArrayList<Field> fields = new ArrayList<>();
    Annotations annotations = new Annotations();
    fill(fields, "", "", (ObjectNode) type.get("fields"), annotations, root);
    return new FieldList(fields);
  }

  private static void fill(ArrayList<Field> fields, String namePrefix, String pathPrefix, ObjectNode type, Annotations annotations, ObjectNode root) {
    Iterator<Map.Entry<String, JsonNode>> it = type.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> val = it.next();
      add(fields, namePrefix + val.getKey(), pathPrefix + val.getKey(), (ObjectNode) val.getValue().get("type"), annotations, root);
    }
  }

  private static void add(ArrayList<Field> fields, String name, String path, ObjectNode type, Annotations annotations, ObjectNode root) {
    ArrayNode an = ((ArrayNode) type.get("annotations"));
    String nature = type.get("nature").textValue();
    if (nature != null) {
      switch (nature) {
        case "reactive_table":
          return;
        case "native_ref":
        case "reactive_ref":
          String typeToFind = type.get("ref").textValue();
          add(fields, name, path, (ObjectNode) root.get(typeToFind), annotations, root);
          return;
        case "reactive_value":
        case "native_value": {
          Field f = new Field(name, path, type.get("type").textValue(), annotations.of(an));
          if (type.has("options")) {
            StringBuilder optionsHtml = new StringBuilder();
            ObjectNode options = (ObjectNode) type.get("options");
            Iterator<Map.Entry<String, JsonNode>> it = options.get("values").fields();
            while (it.hasNext()) {
              Map.Entry<String, JsonNode> entry = it.next();
              boolean selected = entry.getKey().equals(options.get("default").textValue());
              optionsHtml.append("<option value=\"").append(entry.getValue().intValue()).append("\"").append(selected ? " selected" : "").append(">").append(entry.getKey()).append("</option>");
            }
            f.setOptions(optionsHtml.toString());
          }
          fields.add(f);
          return;
        }
        case "native_maybe":
          add(fields, name, path, (ObjectNode) type.get("type"), annotations.of(an), root);
          return;
        case "native_message":
        case "reactive_record":
          fill(fields, name + ".", path + "/", (ObjectNode) type.get("fields"), annotations.of(an), root);
          return;
      }
    }
    LOG.error("unknown-rule:" + name + "," + path + ":" + an + "::" + type);
  }

  public static FieldList intersect(FieldList as, FieldList bs) {
    ArrayList<Field> fields = new ArrayList<>();
    for (Field a : as.fields) {
      Field b = bs.fieldsIndexed.get(a.name);
      if (b != null) {
        if (a.type.equals(b.type)) {
          fields.add(Field.union(a, b));
        }
      }
    }
    return new FieldList(fields);
  }

  @Override
  public Iterator<Field> iterator() {
    return fields.iterator();
  }
}
