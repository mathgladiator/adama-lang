/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.edhtml.phases.generate;

import java.util.HashMap;
import java.util.Map;

/** a field within a type */
public class Field {
  public final String name;
  public final String path;
  public final String type;
  public final Annotations annotations;
  private String options;

  public Field(String name, String path, String type, Annotations annotations) {
    this.name = name;
    this.path = path;
    this.type = type;
    this.annotations = annotations;
    this.options = null;
  }

  public static Field union(Field a, Field b) {
    Field f = new Field(a.name, a.path, a.type, Annotations.union(a.annotations, b.annotations));
    if (a.options != null) {
      f.setOptions(a.options);
    }
    if (b.options != null) {
      f.setOptions(b.options);
    }
    return f;
  }

  public void setOptions(String options) {
    this.options = options;
  }

  public HashMap<String, String> map(HashMap<String, String> globalDefines) {
    HashMap<String, String> result = new HashMap<>(globalDefines);
    result.put("name", this.name);
    result.put("path", this.path);
    result.put("type", this.type);
    if (this.options != null) {
      result.put("options", this.options);
    }
    result.put("annotation:label", this.name);
    result.put("annotation:placeholder", this.name + "...");
    for (Map.Entry<String, String> annotationEntry : annotations) {
      result.put("annotation:" + annotationEntry.getKey().toLowerCase(), annotationEntry.getValue());
    }
    return result;
  }
}
