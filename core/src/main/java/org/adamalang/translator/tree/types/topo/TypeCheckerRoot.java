/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.topo;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;

/** This is a hack class to introduce topological ordering as this is going to be a very complex change */
public class TypeCheckerRoot implements TypeChecker {
  private final ArrayList<Consumer<Environment>> typeCheckOrder;

  public TypeCheckerRoot(ArrayList<Consumer<Environment>> typeCheckOrder) {
    this.typeCheckOrder = typeCheckOrder;
  }

  @Override
  public void define(Token name, Set<String> depends, Consumer<Environment> checker) {
    typeCheckOrder.add(checker);
  }

  @Override
  public void register(Set<String> depends, Consumer<Environment> checker) {
    typeCheckOrder.add(checker);
  }

  public void alias(String from, String to) {

  }

  @Override
  public void issueError(DocumentPosition dp, String message, String tutorial) {
    typeCheckOrder.add(env -> {
      env.document.createError(dp, message, tutorial);
    });
  }
}
