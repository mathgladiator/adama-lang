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
  public void issueError(DocumentPosition dp, String message) {
    this.checks.add(new Check(null, null, env -> {
      env.document.createError(dp, message);
    }));
  }

  public void transferInto(String name, StorageSpecialization specialization, TypeCheckerRoot rootChecker) {
    Once<Environment> onceEnv = new Once<>();
    for (final Check check : checks) {
      check.transferInto(name, onceEnv, specialization, rootChecker);
    }
  }
}
