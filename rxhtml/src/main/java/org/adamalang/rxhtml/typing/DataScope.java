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
package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.rxhtml.template.sp.PathVisitor;

import java.util.Stack;
import java.util.function.Consumer;

/** a path/scope into a forest */
public class DataScope {
  private final ObjectNode forest;
  private final String[] path;

  private DataScope(ObjectNode forest, String[] path) {
    this.forest = forest;
    this.path = path;
  }

  public Stack<String> toStackPath() {
    Stack<String> stack = new Stack<>();
    for (String p : path) {
      stack.push(p);
    }
    return stack;
  }

  public boolean hasChannel(String channel) {
    return forest.get("channels").has(channel);
  }

  public static DataScope root(ObjectNode forest) {
    return new DataScope(forest, new String[] { "__Root" });
  }

  public DataScope push(String... append) {
    String[] next = new String[path.length + append.length];
    for (int k = 0; k < path.length; k++) {
      next[k] = path[k];
    }
    for (int k = 0; k < append.length; k++) {
      next[path.length + k] = append[k];
    }
    return new DataScope(forest, next);
  }

  public DataSelector select(PrivacyFilter privacy, String path, Consumer<String> reportError) {
    DataScopeVisitor dsv = new DataScopeVisitor(path, privacy, forest, toStackPath());
    PathVisitor.visit(path, dsv);
    if (dsv.didSwitchToView()) {
      return null;
    }
    if (dsv.hasErrors()) {
      for (String error : dsv.getErrors()) {
        reportError.accept(error);
      }
      return null;
    }
    String[] newPath = dsv.destroyAndConvertIntoPath();
    DataScope next = new DataScope(forest, newPath);
    return new DataSelector(next, dsv.getUseType());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (String p : path) {
      sb.append("[").append(p).append("]");
    }
    return sb.toString();
  }
}
