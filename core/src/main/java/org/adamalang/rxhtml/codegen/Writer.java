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
