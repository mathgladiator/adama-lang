/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.deploy;

import org.adamalang.common.Json;
import org.adamalang.runtime.json.JsonStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Basic linter to compare two versions of Adama */
public class Linter {
  private static void pumpFieldCompare(String name, String field, HashMap<String, Object> fieldTypeFrom, HashMap<String, Object> fieldTypeTo, ArrayList<String> diagnostics) {
    if ("reactive_maybe".equals(fieldTypeFrom.get("nature"))) {
      if ("reactive_maybe".equals(fieldTypeTo.get("nature"))) {
        pumpFieldCompare(name, field, (HashMap<String, Object>) fieldTypeFrom.get("type"), (HashMap<String, Object>) fieldTypeTo.get("type"), diagnostics);
        return;
      } else {
        pumpFieldCompare(name, field, (HashMap<String, Object>) fieldTypeFrom.get("type"), fieldTypeTo, diagnostics);
        if (name.equals("__Root")) {
          diagnostics.add("Field '" + field + "' within the root document is dropping the maybe and this may result in data invention of default data.");
        } else {
          diagnostics.add("Field '" + field + "' within '" + name + "' is dropping the maybe and this may result in data invention of default data.");
        }
      }
    }

    // reactive_maybe
    if ("reactive_value".equals(fieldTypeFrom.get("nature"))) {
      if ("reactive_maybe".equals(fieldTypeTo.get("nature"))) {
        pumpFieldCompare(name, field, fieldTypeFrom, (HashMap<String, Object>) fieldTypeTo.get("type"), diagnostics);
        return;
      }
      if ("reactive_value".equals(fieldTypeTo.get("nature"))) {
        String fromType = (String) fieldTypeFrom.get("type");
        String toType = (String) fieldTypeTo.get("type");
        if ("long".equals(fromType) && "int".equals(toType)) {
          if (name.equals("__Root")) {
            diagnostics.add("Field '" + field + "' within the root document is being compacted from long to int and may result in data loss.");
          } else {
            diagnostics.add("Field '" + field + "' within '" + name + "' is being compacted from long to int and may result in data loss.");
          }
        }
        if ("string".equals(fromType) && !"string".equals(toType)) {
          if (name.equals("__Root")) {
            diagnostics.add("Field '" + field + "' within the root document is change from a string to a " + toType + " which may lose data.");
          } else {
            diagnostics.add("Field '" + field + "' within '" + name + "' is change from a string to a " + toType + " which may lose data.");
          }
        }
        if ("long".equals(fromType) && "double".equals(toType)) {
          if (name.equals("__Root")) {
            diagnostics.add("Field '" + field + "' within the root document is being compacted from long to double and may result in data precision.");
          } else {
            diagnostics.add("Field '" + field + "' within '" + name + "' is being compacted from long to double and may result in data precision.");
          }
        }
      }
    }
  }

  private static void pumpCompareIssuesStructure(String name, HashMap<String, Object> fieldsFrom, HashMap<String, Object> fieldsTo, ArrayList<String> diagnostics) {
    for (Map.Entry<String, Object> fieldFromEntry : fieldsFrom.entrySet()) {
      HashMap<String, Object> fieldFrom = (HashMap<String, Object>) fieldFromEntry.getValue();
      HashMap<String, Object> fieldTo = (HashMap<String, Object>) fieldsTo.get(fieldFromEntry.getKey());
      if (fieldTo != null) {
        pumpFieldCompare(name, fieldFromEntry.getKey(), (HashMap<String, Object>) fieldFrom.get("type"), (HashMap<String, Object>) fieldTo.get("type"), diagnostics);
      }
    }
  }

  private static void pumpCompareIssuesEnum(String type, HashMap<String, Object> valuesFrom, HashMap<String, Object> valuesTo, ArrayList<String> diagnostics) {
    for (Map.Entry<String, Object> fromEntry : valuesFrom.entrySet()) {
      int fromValue = (int) fromEntry.getValue();
      Integer toValue = (Integer) valuesTo.get(fromEntry.getKey());
      if (toValue != null) {
        if (fromValue != toValue) {
          diagnostics.add("Enumeration '" + type + "' has a label change for '" + fromEntry.getKey() + " from " + fromValue + " to " + toValue);
        }
      }
    }
  }

  public static ArrayList<String> compare(String reflectionFrom, String reflectionTo) {
    System.err.println(Json.parseJsonObject(reflectionFrom).toPrettyString());
    System.err.println(Json.parseJsonObject(reflectionTo).toPrettyString());
    ArrayList<String> diagnostics = new ArrayList<>();
    HashMap<String, Object> rootFrom = (HashMap<String, Object>) new JsonStreamReader(reflectionFrom).readJavaTree();
    HashMap<String, Object> rootTo = (HashMap<String, Object>) new JsonStreamReader(reflectionTo).readJavaTree();
    HashMap<String, Object> typesFrom = (HashMap<String, Object>) rootFrom.get("types");
    HashMap<String, Object> typesTo = (HashMap<String, Object>) rootTo.get("types");
    for (String type : typesFrom.keySet()) {
      HashMap<String, Object> typeFrom = (HashMap<String, Object>) typesFrom.get(type);
      HashMap<String, Object> typeTo = (HashMap<String, Object>) typesTo.get(type);
      if (typeTo != null) { // a change in the type
        String nature = (String) typeFrom.get("nature");
        if ("native_value".equals(nature)) {
          if ("enum".equals(typeFrom.get("type"))) {
            if ("enum".equals(typeTo.get("type")) && "native_value".equals(typeTo.get("nature"))) {
              HashMap<String, Object> optionsFrom = (HashMap<String, Object>) typeFrom.get("options");
              HashMap<String, Object> optionsTo = (HashMap<String, Object>) typeTo.get("options");
              pumpCompareIssuesEnum(type, (HashMap<String, Object>) optionsFrom.get("values"), (HashMap<String, Object>) optionsTo.get("values"), diagnostics);
            }
          }
        }
        if ("reactive_record".equals(nature) || "native_message".equals(nature)) {
          HashMap<String, Object> fieldsFrom = (HashMap<String, Object>) typeFrom.get("fields");
          HashMap<String, Object> fieldsTo = (HashMap<String, Object>) typeTo.get("fields");
          pumpCompareIssuesStructure(type, fieldsFrom, fieldsTo, diagnostics);
        }
      }
    }
    return diagnostics;
  }
}
