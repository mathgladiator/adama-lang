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
package org.adamalang.translator.tree.types.topo;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;

/** This is a hack class to introduce topological ordering as this is going to be a very complex change */
public class TypeCheckerRoot implements TypeChecker {
  private final ArrayList<Check> checks;
  private HashMap<String, ArrayList<String>> aliases;
  private final HashMap<String, Check> byName;

  private class Check {
    private final String name;
    private final Set<String> depends;
    private final Consumer<Environment> checker;

    private boolean handled;

    public Check(String name, Set<String> depends, Consumer<Environment> checker) {
      this.name = name;
      this.depends = depends;
      this.checker = checker;
      this.handled = false;
    }
  }

  public TypeCheckerRoot() {
    this.checks = new ArrayList<>();
    this.aliases = new HashMap<>();
    this.byName = new HashMap<>();
  }

  @Override
  public void define(Token name, Set<String> depends, Consumer<Environment> checker) {
    Check check = new Check(name.text, depends, checker);
    this.checks.add(check);
    byName.put(name.text, check);
  }

  @Override
  public void register(Set<String> depends, Consumer<Environment> checker) {
    this.checks.add(new Check(null, depends, checker));
  }

  public void alias(String from, String to) {
    ArrayList<String> alias = aliases.get(from);
    if (alias == null) {
      alias = new ArrayList<>();
      aliases.put(from, alias);
    }
    alias.add(to);
  }

  @Override
  public void issueError(DocumentPosition dp, String message) {
    this.checks.add(new Check(null, null, env -> env.document.createError(dp, message)));
  }


  private void satisfyOneChild(String name, Environment environment) {
    Check check = byName.get(name);
    if (check == null) {
      return;
    }
    if (!check.handled) {
      satisfyAllChildren(check, environment);
      check.checker.accept(environment);
    }
  }

  private void satisfyAllChildren(Check check, Environment environment) {
    check.handled = true;
    if (check.depends != null) {
      for (String depend : check.depends) {
        ArrayList<String> prior = aliases.get(depend);
        if (prior != null) {
          for (String child : prior) {
            satisfyOneChild(child, environment);
          }
        }
        satisfyOneChild(depend, environment);
      }
    }
  }

  public void check(Environment environment) {
    while (checks.size() > 0) {
      Check check = checks.remove(0);
      if (!check.handled) {
        satisfyAllChildren(check, environment);
        check.checker.accept(environment);
      }
    }
  }
}
