/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
