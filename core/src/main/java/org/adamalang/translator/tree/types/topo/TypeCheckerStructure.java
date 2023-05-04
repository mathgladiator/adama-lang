package org.adamalang.translator.tree.types.topo;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

public class TypeCheckerStructure {
  private final ArrayList<Consumer<Environment>> typeCheckOrder;

  public TypeCheckerStructure() {
    this.typeCheckOrder = new ArrayList<>();
  }

  public void define(Token name, Set<String> depends, Consumer<Environment> checker) {
    typeCheckOrder.add(checker);
  }

  public void register(Set<String> depends, Consumer<Environment> checker) {
    typeCheckOrder.add(checker);
  }

  public void issueError(DocumentPosition dp, String message, String tutorial) {
    typeCheckOrder.add(env -> {
      env.document.createError(dp, message, tutorial);
    });
  }
}
