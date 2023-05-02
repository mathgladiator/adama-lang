/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.rxhtml.codegen;

/** a tab-friendly wrapper around StringBuilder */
public class Writer {
  private final StringBuilder sb = new StringBuilder();
  private int tabAt;
  private String tabCache;

  public Writer() {
    tabAt = 0;
    tabCache = "";
  }

  public Writer tabUp() {
    tabAt++;
    rebuildTabCache();
    return this;
  }

  private void rebuildTabCache() {
    StringBuilder tabCacheBuilder = new StringBuilder();
    for (int k = 0; k < tabAt; k++) {
      tabCacheBuilder.append("  ");
    }
    tabCache = tabCacheBuilder.toString();
  }

  public Writer tabDown() {
    tabAt--;
    rebuildTabCache();
    return this;
  }

  public Writer tab() {
    sb.append(tabCache);
    return this;
  }

  public Writer append(String s) {
    sb.append(s);
    return this;
  }

  public Writer newline() {
    sb.append("\n");
    return this;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}
