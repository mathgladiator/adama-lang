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
package org.adamalang.rxhtml.codegen;

import java.util.Iterator;
import java.util.TreeSet;

public class VariablePool {
  private final char[] basis;
  private final TreeSet<String> pool;
  private int at;

  public VariablePool() {
    this.at = 0;
    this.pool = new TreeSet<>();
    this.basis = new char[26];
    for (int k = 0; k < 26; k++) {
      this.basis[k] = (char) ('a' + k);
    }
  }

  public String ask() {
    Iterator<String> it = pool.iterator();
    if (it.hasNext()) {
      String result = it.next();
      it.remove();
      return result;
    }
    String result = make(at);
    at++;
    return result;
  }

  private String make(int idx) {
    StringBuilder sb = new StringBuilder();
    int v = idx;
    while (v > 0 || sb.length() == 0) {
      sb.append(basis[v % 26]);
      v /= 26;
    }
    String x = sb.toString();
    switch (x) { // TODO: add a more complete list of javascript keywords
      case "if":
      case "in":
      case "int":
      case "var":
      case "new":
      case "do":
      case "for":
        return "_" + x;
      default:
        return x;
    }
  }

  public void give(String p) {
    pool.add(p);
  }
}
