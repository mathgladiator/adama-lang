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
package org.adamalang.translator.tree.types;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.WatchSet;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.TyNativeService;
import org.adamalang.translator.tree.types.natives.TyNativeTemplate;
import org.adamalang.translator.tree.types.reactive.TyReactiveMap;
import org.adamalang.translator.tree.types.reactive.TyReactiveTable;

import java.util.LinkedHashSet;
import java.util.function.BiConsumer;

public class Watcher {
  public static BiConsumer<String, TyType> makeAuto(Environment env, WatchSet watching) {    return (name, type) -> {
      TyType resolved = env.rules.Resolve(type, true);
      if (resolved instanceof TyNativeGlobalObject) return;
      if (resolved instanceof TyNativeTemplate) return;
      if (resolved instanceof TyNativeFunctional) {
        watching.variables.addAll(((TyNativeFunctional) resolved).gatherDependencies());
        return;
      }
      if (resolved instanceof TyNativeService) {
        watching.services.add(((TyNativeService) resolved).service.name.text);
        return;
      }
      if (!env.document.functionTypes.containsKey(name)) {
        if (type instanceof TyReactiveMap) {
          watching.maps.add(name);
          watching.pubsub.add(name);
        } else if (type instanceof TyReactiveTable) {
          watching.tables.add(name);
          watching.pubsub.add(name);
        } else {
          watching.variables.add(name);
        }
      }
    };
  }

  public static BiConsumer<String, TyType> makeAutoSimple(Environment env, LinkedHashSet<String> variables, LinkedHashSet<String> services) {    return (name, type) -> {
    TyType resolved = env.rules.Resolve(type, true);
    if (resolved instanceof TyNativeGlobalObject) return;
    if (resolved instanceof TyNativeTemplate) return;
    if (resolved instanceof TyNativeFunctional) {
      variables.addAll(((TyNativeFunctional) resolved).gatherDependencies());
      return;
    }
    if (resolved instanceof TyNativeService) {
      services.add(((TyNativeService) resolved).service.name.text);
      return;
    }
    if (!env.document.functionTypes.containsKey(name)) {
      variables.add(name);
    }
  };
  }
}
