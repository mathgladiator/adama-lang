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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.rxhtml.template.sp.PathVisitor;

import java.util.ArrayList;
import java.util.Stack;

/** visitor for working with paths within a DataScope; this is how to navigate and select data from within a scope */
public class DataScopeVisitor implements PathVisitor {
  private final String path;
  private final PrivacyFilter privacy;
  private final ObjectNode forest;
  private final Stack<String> current;
  private ObjectNode useType;
  private boolean switchToView;
  private ArrayList<String> errors;

  public DataScopeVisitor(String path, PrivacyFilter privacy, ObjectNode forest, Stack<String> current) {
    this.path = path;
    this.privacy = privacy;
    this.forest = forest;
    this.current = current;
    this.switchToView = false;
    this.errors = new ArrayList<>();
  }

  public String[] destroyAndConvertIntoPath() {
    String[] path = new String[current.size()];
    int k = path.length - 1;
    while (!current.empty()) {
      path[k] = current.pop();
      k--;
    }
    return path;
  }

  @Override
  public void data() {

  }

  @Override
  public void view() {
    this.switchToView = true;
  }

  @Override
  public void root() {
    while (current.size() > 1) {
      current.pop();
    }
  }

  @Override
  public void parent() {
    if (current.size() > 1) {
      current.pop();
    }
  }

  private ObjectNode getCurrentChildType(String child) {
    String at = current.peek();
    boolean isList = false; // spec work
    if (at.startsWith("list:")) {
      isList = true;
      at = at.substring(5);
    }
    ObjectNode holder = (ObjectNode) forest.get("types").get(at);
    if (holder == null) {
      errors.add("Failed to find type '" + at + "'");
      return null;
    }

    if (isList) {
      try {
        Integer.parseInt(child);
        // it's all good, extract the embedded list type
        errors.add("Parsed, but not yet implemented to dive into an index within an list");
        return null;
      } catch (NumberFormatException nfe) {
        errors.add("Failed to parse '" + child + "' as int within '" + at + "' (which is a list/array)");
        return null;
      }
    } else {
      ObjectNode childType = (ObjectNode) holder.get("fields").get(child);
      if (childType == null) {
        errors.add("Failed to type '" + child + "' within '" + at + "'");
        return null;
      }
      JsonNode privacyLevel = childType.get("privacy");
      if (!privacy.visible(privacyLevel)) {
        errors.add("Privacy violation; field '" + child + "' was referenced, but is not visible within the privacy filter: " + privacyLevel.toString());
        // TODO: make above error more meaningful
      }
      childType.put("used", true);
      return (ObjectNode) childType.get("type");
    }
  }

  @Override
  public void dive(String child) {
    ObjectNode nextType = getCurrentChildType(child);
    if (nextType == null) {
      return;
    }

    String nature = nextType.get("nature").textValue();

    if (nature.equals("native_maybe") || nature.equals("reactive_maybe")) {
      nextType = (ObjectNode) nextType.get("type");
      nature = nextType.get("nature").textValue();
    }

    if (nature.equals("native_list") || nature.equals("native_array")) {
      ObjectNode subType = (ObjectNode) nextType.get("type");
      String subNature = subType.get("nature").textValue();
      if (subNature.equals("reactive_ref") || subNature.equals("native_ref")) {
        String ref = subType.get("ref").textValue();
        current.push("list:" + ref);
      } else  {
        errors.add("failed to understand list sub-type:" + subType);
      }
    } else if (nature.equals("native_ref") || nature.equals("reactive_ref")) {
      String ref = nextType.get("ref").textValue();
      current.push(ref);
    } else {
      errors.add("failed to understand type:" + nextType);
    }
  }

  @Override
  public void use(String field) {
    this.useType = getCurrentChildType(field);
  }

  public ObjectNode getUseType() {
    return this.useType;
  }

  public boolean hasErrors() {
    return errors.size() > 0;
  }

  public ArrayList<String> getErrors() {
    return errors;
  }

  public boolean didSwitchToView() {
    return switchToView;
  }
}
