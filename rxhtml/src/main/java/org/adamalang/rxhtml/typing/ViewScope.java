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
package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.rxhtml.template.StatePath;
import org.adamalang.rxhtml.template.sp.PathInstruction;
import org.adamalang.rxhtml.template.sp.SwitchTo;

import java.util.Map;
import java.util.TreeMap;

/** scope for constructing a view object based on inference */
public class ViewScope {
  public final ViewScope parent;
  public final TreeMap<String, String> types;
  public final TreeMap<String, ViewScope> children;

  private ViewScope(ViewScope parent) {
    this.parent = parent;
    this.types = new TreeMap<>();
    this.children = new TreeMap<>();
  }

  public void fill(ObjectNode node) {
    for (Map.Entry<String, String> entry : types.entrySet()) {
      node.put(entry.getKey(), entry.getValue());
    }
    for (Map.Entry<String, ViewScope> entry : children.entrySet()) {
      entry.getValue().fill(node.putObject(entry.getKey()));
    }
  }

  public ViewScope eval(String pathing) {
    StatePath sp = StatePath.resolve(pathing, "$");
    ViewScope current = this;
    for (PathInstruction instruction : sp.instructions) {
      if (current != null) {
        current = instruction.next(current);
      }
    }
    return current.child(sp.name);
  }

  public void write(String pathing, String type, boolean checkForViewSwitch) {
    StatePath sp = StatePath.resolve(pathing, "$");
    ViewScope current = this;
    boolean foundView = false;
    for (PathInstruction instruction : sp.instructions) {
      if (instruction instanceof SwitchTo) {
        if ("view".equals(((SwitchTo) instruction).dest)) {
          foundView = true;
        }
      }
      if (current != null) {
        current = instruction.next(current);
      }
    }
    if (!foundView && checkForViewSwitch) {
      return;
    }
    if (current != null) {
      String prior = current.types.get(sp.name);
      if (prior == null || "lookup".equals(prior)) {
        current.types.put(sp.name, type);
        // TODO: convert the map to a set such that we can see all the type interactions? consider it
      }
    }
  }

  public ViewScope child(String name) {
    ViewScope result = children.get(name);
    if (result == null) {
      result = new ViewScope(this);
      children.put(name, result);
    }
    return result;
  }

  public static ViewScope makeRoot() {
    return new ViewScope(null);
  }
}
