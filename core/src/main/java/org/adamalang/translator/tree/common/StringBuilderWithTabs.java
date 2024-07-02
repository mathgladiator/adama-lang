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
