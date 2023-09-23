package org.adamalang.rxhtml.typing;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.rxhtml.template.StatePath;
import org.adamalang.rxhtml.template.sp.PathInstruction;

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

  public void write(String pathing, String type) {
    StatePath sp = StatePath.resolve(pathing, "$");
    ViewScope current = this;
    for (PathInstruction instruction : sp.instructions) {
      if (current != null) {
        current = instruction.next(current);
      }
    }
    current.types.put(sp.name, type);
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
