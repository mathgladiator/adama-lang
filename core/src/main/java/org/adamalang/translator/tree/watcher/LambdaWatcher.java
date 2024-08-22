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
import org.adamalang.translator.env.GlobalObjectPool;
import org.adamalang.translator.tree.types.TyType;

import java.util.TreeMap;

/** common: watch an environment for types that flow */
public class LambdaWatcher implements Watcher {
  private final Environment environment;
  private final TreeMap<String, TyType> closureTyTypes;
  private final TreeMap<String, String> closureTypes;

  public LambdaWatcher(Environment environment, TreeMap<String, TyType> closureTyTypes, TreeMap<String, String> closureTypes) {
    this.environment = environment;
    this.closureTyTypes = closureTyTypes;
    this.closureTypes = closureTypes;
  }

  @Override
  public void observe(String name, TyType type) {
    TyType ty = environment.rules.Resolve(type, false);
    if (GlobalObjectPool.ignoreCapture(name, ty)) {
      return;
    }
    if (!closureTypes.containsKey(name) && ty != null) {
      closureTyTypes.put(name, ty);
      closureTypes.put(name, ty.getJavaConcreteType(environment));
    }
  }

  @Override
  public void assoc(String name) {

  }
}
