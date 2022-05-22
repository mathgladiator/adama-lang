/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.rxhtml.codegen;

import java.util.Iterator;
import java.util.TreeSet;

public class VariablePool {
  private final char[] basis;
  private int at;
  private final TreeSet<String> pool;

  public VariablePool() {
    this.at = 0;
    this.pool = new TreeSet<>();
    this.basis = new char[26];
    for(int k = 0; k < 26; k++) {
      this.basis[k] = (char) ('a' + k);
    }
  }

  private String make(int idx) {
    StringBuilder sb = new StringBuilder();
    int v = idx;
    while (v > 0 || sb.length() == 0) {
      sb.append(basis[v % 26]);
      v /= 26;
    }
    return sb.toString();
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

  public void give(String p) {
    pool.add(p);
  }
}
