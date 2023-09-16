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
package org.adamalang.common.template.tree;

import com.fasterxml.jackson.databind.JsonNode;
import org.adamalang.common.template.Settings;

import java.util.ArrayList;

/** concat multiple T together */
public class TConcat implements T {
  private final ArrayList<T> children;

  public TConcat() {
    this.children = new ArrayList<>();
  }

  public void add(T child) {
    this.children.add(child);
  }

  @Override
  public void render(Settings settings, JsonNode node, StringBuilder output) {
    for (T child : children) {
      child.render(settings, node, output);
    }
  }
}
