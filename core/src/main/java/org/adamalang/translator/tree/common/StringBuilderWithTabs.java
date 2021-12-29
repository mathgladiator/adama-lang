/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.common;

import java.util.ArrayList;

/**
 * this is the janky way we do build code, and it brings great shame. Fixing this will require a
 * massive overhaul, and the problem is just how hard it is to make a reasonable formatted code.
 */
public class StringBuilderWithTabs {
  private final StringBuilder builder;
  private int tabs;

  public StringBuilderWithTabs() {
    builder = new StringBuilder();
    tabs = 0;
  }

  /** append the given string */
  public StringBuilderWithTabs append(final String x) {
    builder.append(x);
    return this;
  }

  /** insert a tab UNCLEAN */
  public StringBuilderWithTabs tab() {
    builder.append("  ");
    return this;
  }

  /** decrease the tab */
  public StringBuilderWithTabs tabDown() {
    tabs--;
    if (tabs < 0) {
      tabs = 0;
    }
    return this;
  }

  /** increase the tabs */
  public StringBuilderWithTabs tabUp() {
    tabs++;
    return this;
  }

  /** split the builder into lines */
  public ArrayList<String> toLines() {
    final var lines = new ArrayList<String>();
    for (final String line : toString().split("\n")) {
      lines.add(line);
    }
    return lines;
  }

  @Override
  public String toString() {
    return builder.toString();
  }

  /** append a new line */
  public StringBuilderWithTabs writeNewline() {
    builder.append("\n");
    for (var k = 0; k < tabs; k++) {
      builder.append("  ");
    }
    return this;
  }
}
