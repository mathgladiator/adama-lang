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
package org.adamalang.translator.tree.watcher;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.TyNativeService;

import java.util.Set;

/** specific: collect the dependencies of all functions */
public class FunctionalWatcher implements Watcher {
  private final Environment environment;
  private final Set<String> depends;
  private final Set<String> assocs;

  public FunctionalWatcher(Environment environment, Set<String> depends, Set<String> assocs) {
    this.environment = environment;
    this.depends = depends;
    this.assocs = assocs;
  }

  @Override
  public void observe(String name, TyType type) {
    TyType resolved = environment.rules.Resolve(type, true);
    if (resolved instanceof TyNativeGlobalObject) return;
    if (resolved instanceof TyNativeFunctional) {
      depends.addAll(((TyNativeFunctional) resolved).gatherDependencies());
      assocs.addAll(((TyNativeFunctional) resolved).gatherAssocs());
      return;
    }
    if (resolved instanceof TyNativeService) return;
    depends.add(name);
  }

  @Override
  public void assoc(String name) {
    assocs.add(name);
  }
}
