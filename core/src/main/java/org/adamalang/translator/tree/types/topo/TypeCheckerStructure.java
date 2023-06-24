/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.topo;

import org.adamalang.common.Once;
import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.structures.StorageSpecialization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class TypeCheckerStructure implements TypeChecker {
  private class Check {
    public final String name;
    public final Set<String> depends;
    public final Consumer<Environment> checker;

    public Check(String name, Set<String> depends, Consumer<Environment> checker) {
      this.name = name;
      this.depends = depends;
      this.checker = checker;
    }

    public void transferInto(String name, Once<Environment> onceEnv, StorageSpecialization specialization, TypeCheckerRoot rootChecker) {
      Consumer<Environment> modern = (stale) -> {
        Environment envToUse = onceEnv.access(() -> specialization == StorageSpecialization.Message ? stale.scope().scopeMessage() : stale.scope());
        checker.accept(envToUse);
      };

      if (name == null && depends == null) {
        rootChecker.register(Collections.emptySet(), modern);
      }

      HashSet<String> translated = new HashSet<>();
      if (depends != null) {
        for (String depend : depends) {
          if (defined.contains(depend)) {
            translated.add(name + "::" + depend);
          } else {
            translated.add(depend);
          }
        }
      }

      if (this.name != null) {
        rootChecker.define(Token.WRAP(name + "::" + this.name), translated, modern);
        rootChecker.alias("::" + this.name, name + "::" + this.name);
      } else {
        rootChecker.register(translated, modern);
      }
    }
  }

  private final ArrayList<Check> checks;
  private final HashSet<String> defined;

  public TypeCheckerStructure() {
    this.checks = new ArrayList<>();
    this.defined = new HashSet<>();
  }

  @Override
  public void define(Token name, Set<String> depends, Consumer<Environment> checker) {
    this.checks.add(new Check(name.text, depends, checker));
    this.defined.add(name.text);
  }

  @Override
  public void register(Set<String> depends, Consumer<Environment> checker) {
    this.checks.add(new Check(null, depends, checker));
  }

  @Override
  public void issueError(DocumentPosition dp, String message, String tutorial) {
    this.checks.add(new Check(null, null, env -> {
      env.document.createError(dp, message, tutorial);
    }));
  }

  public void transferInto(String name, StorageSpecialization specialization, TypeCheckerRoot rootChecker) {
    Once<Environment> onceEnv = new Once<>();
    for (final Check check : checks) {
      check.transferInto(name, onceEnv, specialization, rootChecker);
    }
  }
}
