/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.deploy;

import org.adamalang.runtime.json.JsonStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Basic linter to compare two versions of Adama */
public class Linter {
  private final HashMap<String, Object> rootFrom;
  private final HashMap<String, Object> rootTo;
  private final HashMap<String, Object> typesFrom;
  private final HashMap<String, Object> typesTo;
  private final ArrayList<String> diagnostics;

  private Linter(String reflectionFrom, String reflectionTo) {
    this.rootFrom = (HashMap<String, Object>) new JsonStreamReader(reflectionFrom).readJavaTree();
    this.rootTo = (HashMap<String, Object>) new JsonStreamReader(reflectionTo).readJavaTree();
    this.typesFrom = (HashMap<String, Object>) rootFrom.get("types");
    this.typesTo = (HashMap<String, Object>) rootTo.get("types");
    this.diagnostics = new ArrayList<>();
  }

  public static ArrayList<String> compare(String reflectionFrom, String reflectionTo) {
    System.err.println(reflectionFrom);
    System.err.println(reflectionTo);
    Linter linter = new Linter(reflectionFrom, reflectionTo);
    linter.start();
    return linter.diagnostics;
  }

  private void pumpFieldCompare(String what, HashMap<String, Object> fieldTypeFromRaw, HashMap<String, Object> fieldTypeToRaw) {
    HashMap<String, Object> fieldTypeFrom = fieldTypeFromRaw;
    HashMap<String, Object> fieldTypeTo = fieldTypeToRaw;
    if ("reactive_ref".equals(fieldTypeFrom.get("nature"))) {
      fieldTypeFrom = (HashMap<String, Object>) typesFrom.get((String) fieldTypeFrom.get("ref"));
    }
    if ("reactive_ref".equals(fieldTypeTo.get("nature"))) {
      fieldTypeTo = (HashMap<String, Object>) typesTo.get((String) fieldTypeTo.get("ref"));
    }

    if ("reactive_record".equals(fieldTypeFrom.get("nature"))) {
      if ("reactive_record".equals(fieldTypeTo.get("nature"))) {
        pumpCompareIssuesStructure("record at " + what, (HashMap<String, Object>) fieldTypeFrom.get("fields"), (HashMap<String, Object>) fieldTypeTo.get("fields"));
        return;
      }
    }

    if ("reactive_maybe".equals(fieldTypeFrom.get("nature"))) {
      if ("reactive_maybe".equals(fieldTypeTo.get("nature"))) {
        pumpFieldCompare(what, (HashMap<String, Object>) fieldTypeFrom.get("type"), (HashMap<String, Object>) fieldTypeTo.get("type"));
        return;
      } else {
        pumpFieldCompare(what, (HashMap<String, Object>) fieldTypeFrom.get("type"), fieldTypeTo);
        diagnostics.add(what + " is dropping the maybe and this may result in data invention of default data.");
        return;
      }
    }

    if ("reactive_table".equals(fieldTypeFrom.get("nature"))) {
      if ("reactive_table".equals(fieldTypeTo.get("nature"))) {
        String recordFrom = (String) fieldTypeFrom.get("record_name");
        String recordTo = (String) fieldTypeTo.get("record_name");
        HashMap<String, Object> fieldsFrom = (HashMap<String, Object>) ((HashMap<String, Object>) typesFrom.get(recordFrom)).get("fields");
        HashMap<String, Object> fieldsTo = (HashMap<String, Object>) ((HashMap<String, Object>) typesTo.get(recordTo)).get("fields");
        pumpCompareIssuesStructure("table at " + what, fieldsFrom, fieldsTo);
      } else {
        diagnostics.add(what + " is a table dropping to another type.");
      }
    }


    // reactive_maybe
    if ("reactive_value".equals(fieldTypeFrom.get("nature"))) {
      if ("reactive_maybe".equals(fieldTypeTo.get("nature"))) {
        pumpFieldCompare(what, fieldTypeFrom, (HashMap<String, Object>) fieldTypeTo.get("type"));
        return;
      }
      if ("reactive_value".equals(fieldTypeTo.get("nature"))) {
        String fromType = (String) fieldTypeFrom.get("type");
        String toType = (String) fieldTypeTo.get("type");
        if ("long".equals(fromType) && "int".equals(toType)) {
          diagnostics.add(what + " is being compacted from long to int and may result in data loss.");
        } else if ("string".equals(fromType) && !"string".equals(toType)) {
          diagnostics.add(what + " is change from a string to a " + toType + " which may lose data.");
        } else if ("principal".equals(fromType) && !"principal".equals(toType)) {
          diagnostics.add(what + " is change from a principal to a " + toType + " which will lose data.");
        } else if ("long".equals(fromType) && "double".equals(toType)) {
          diagnostics.add(what + " is being compacted from long to double and may result in data precision.");
        } else if ("long".equals(fromType) && "complex".equals(toType)) {
          diagnostics.add(what + " is being compacted from long to complex and may result in data precision.");
        } else if ("int".equals(fromType) && ("long".equals(toType) || "double".equals(toType) || "complex".equals(toType))) {
          // OK
        } else if ("double".equals(fromType) && "complex".equals(toType)) {
          // OK
        } else if (fromType != null && !fromType.equals(toType)) {
          diagnostics.add(what + " is change from a " + fromType + " to a " + toType + " which will lose data.");
        }
      }
    }
  }

  private void pumpCompareIssuesStructure(String what, HashMap<String, Object> fieldsFrom, HashMap<String, Object> fieldsTo) {
    for (Map.Entry<String, Object> fieldFromEntry : fieldsFrom.entrySet()) {
      HashMap<String, Object> fieldFrom = (HashMap<String, Object>) fieldFromEntry.getValue();
      HashMap<String, Object> fieldTo = (HashMap<String, Object>) fieldsTo.get(fieldFromEntry.getKey());
      if (fieldTo != null) {
        pumpFieldCompare("field '" + fieldFromEntry.getKey() + "' in " + what, (HashMap<String, Object>) fieldFrom.get("type"), (HashMap<String, Object>) fieldTo.get("type"));
      }
    }
  }

  private void pumpCompareIssuesEnum(String what, HashMap<String, Object> valuesFrom, HashMap<String, Object> valuesTo) {
    for (Map.Entry<String, Object> fromEntry : valuesFrom.entrySet()) {
      int fromValue = (int) fromEntry.getValue();
      Integer toValue = (Integer) valuesTo.get(fromEntry.getKey());
      if (toValue != null) {
        if (fromValue != toValue) {
          diagnostics.add(what + " has a label change for '" + fromEntry.getKey() + " from " + fromValue + " to " + toValue + ".");
        }
      }
    }
  }

  private void start() {
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
              pumpCompareIssuesEnum("enumeration '" + type + "'", (HashMap<String, Object>) optionsFrom.get("values"), (HashMap<String, Object>) optionsTo.get("values"));
            }
          }
        }
        if ("reactive_record".equals(nature) || "native_message".equals(nature)) {
          String what = type.equals("__Root") ? "root document" : ("reactive_record".equals(nature) ? ("record '" + type + "'") : ("message '" + type + "'"));
          HashMap<String, Object> fieldsFrom = (HashMap<String, Object>) typeFrom.get("fields");
          HashMap<String, Object> fieldsTo = (HashMap<String, Object>) typeTo.get("fields");
          pumpCompareIssuesStructure(what, fieldsFrom, fieldsTo);
        }
      }
    }
  }
}
