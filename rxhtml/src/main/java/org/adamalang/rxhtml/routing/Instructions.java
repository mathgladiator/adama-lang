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
package org.adamalang.rxhtml.routing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Function;

public class Instructions {
  public final String javascript;
  public final HashSet<String> depends;
  public final String formula;
  public final String normalized;
  public final TreeMap<String, String> types;
  public final ArrayList<Function<Path, Path>> progress;

  public Instructions(final String javascript, HashSet<String> depends, String formula, String normalized, TreeMap<String, String> types, ArrayList<Function<Path, Path>> progress) {
    this.javascript = javascript;
    this.depends = depends;
    this.formula = formula;
    this.normalized = normalized;
    this.types = types;
    this.progress = progress;
  }

  /** convert a raw uri to an instruction set */
  public static Instructions parse(String uriRaw) {
    HashSet<String> depends = new HashSet<>();
    String uri = (uriRaw.startsWith("/") ? uriRaw.substring(1) : uriRaw).trim();
    StringBuilder formula = new StringBuilder();
    formula.append("/");
    TreeMap<String, String> types = new TreeMap<>();
    ArrayList<Function<Path, Path>> progress = new ArrayList<>();
    StringBuilder normalized = new StringBuilder();
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    boolean first = true;
    do {
      int kSlash = uri.indexOf('/');
      String fragment = kSlash >= 0 ? uri.substring(0, kSlash).trim() : uri;
      uri = kSlash >= 0 ? uri.substring(kSlash + 1).trim() : "";
      if (!first) {
        sb.append(",");
      }
      first = false;
      if (fragment.startsWith("$")) {
        boolean suffix = false;
        if (fragment.endsWith("*")) {
          if (uri.equals("")) {
            suffix = true;
          } else {
            fragment = fragment.substring(0, fragment.length() - 1);
          }
        }
        if (suffix) {
          final String name = fragment.substring(1, fragment.length() - 1);
          depends.add(name);
          sb.append("'suffix','").append(name).append("'");
          types.put(name, "text");
          formula.append("{").append(name).append("}");
          normalized.append("/$").append("text");
          progress.add((p) -> p.setSuffix(name));
        } else {
          int colon = fragment.indexOf(':');
          String type = (colon > 0 ? fragment.substring(colon + 1) : "text").trim().toLowerCase();
          final String name = (colon > 0 ? fragment.substring(1, colon) : fragment.substring(1)).trim();
          switch (type) {
            case "number":
            case "int":
            case "double":
              type = "number";
              break;
            default:
              type = "text";
          }
          depends.add(name);
          sb.append("'").append(type).append("','").append(name).append("'");
          types.put(name, type);
          formula.append("{").append(name).append("}");
          normalized.append("/$").append(type);
          if ("number".equals(type)) {
            progress.add((p) -> p.newNumber(name));
          } else {
            progress.add((p) -> p.newText(name));
          }
        }
      } else {
        normalized.append("/").append(fragment);
        sb.append("'fixed','").append(fragment).append("'");
        formula.append(fragment);
        final String fixedFragment = fragment;
        progress.add((p) -> p.diveFixed(fixedFragment));
      }
      if (kSlash >= 0) {
        formula.append("/");
      }
    } while (uri.length() > 0);
    sb.append("]");
    return new Instructions(sb.toString(), depends, formula.toString(), normalized.toString(), types, progress);
  }
}
