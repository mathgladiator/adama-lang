/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml.phases.generate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/** a list of fields that are indexed by path */
public class FieldList implements Iterable<Field> {
  private final ArrayList<Field> fields;
  private final HashMap<String, Field> fieldsIndexed;

  public FieldList(ArrayList<Field> fields) {
    this.fields = fields;
    this.fieldsIndexed = new HashMap<>();
    for (Field f : fields) {
      fieldsIndexed.put(f.name, f);
    }
  }

  public static FieldList of(ObjectNode type) {
    ArrayList<Field> fields = new ArrayList<>();
    Annotations annotations = new Annotations();
    fill(fields, "", "", (ObjectNode) type.get("fields"), annotations);
    return new FieldList(fields);
  }

  private static void fill(ArrayList<Field> fields, String namePrefix, String pathPrefix, ObjectNode type, Annotations annotations) {
    Iterator<Map.Entry<String, JsonNode>> it = type.fields();
    while (it.hasNext()) {
      Map.Entry<String, JsonNode> val = it.next();
      add(fields, namePrefix + val.getKey(), pathPrefix + val.getKey(), (ObjectNode) val.getValue().get("type"), annotations);
    }
  }

  private static void add(ArrayList<Field> fields, String name, String path, ObjectNode type, Annotations annotations) {
    ArrayNode an = ((ArrayNode) type.get("annotations"));
    String nature = type.get("nature").textValue();
    if (nature != null) {
      switch (nature) {
        case "reactive_table":
          return;
        case "reactive_value":
        case "native_value":
          fields.add(new Field(name, path, type.get("type").textValue(), annotations.of(an)));
          return;
        case "native_maybe":
          add(fields, name, path, (ObjectNode) type.get("type"), annotations.of(an));
          return;
        case "native_message":
        case "reactive_record":
          fill(fields, name + ".", path + "/", (ObjectNode) type.get("fields"), annotations.of(an));
          return;
      }
    }
    System.err.println("UNKNOWN:" + name + "," + path + ":" + an + "::" + type);
  }

  public static FieldList intersect(FieldList as, FieldList bs) {
    ArrayList<Field> fields = new ArrayList<>();
    for (Field a : as.fields) {
      Field b = bs.fieldsIndexed.get(a.name);
      if (b != null) {
        fields.add(Field.union(a, b));
      }
    }
    return new FieldList(fields);
  }

  @Override
  public Iterator<Field> iterator() {
    return fields.iterator();
  }
}
