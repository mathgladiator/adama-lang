/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.TyNativeService;

import java.util.LinkedHashSet;
import java.util.function.BiConsumer;

public class Watcher {
  public static BiConsumer<String, TyType> make(Environment env, LinkedHashSet<String> variablesToWatch, LinkedHashSet<String> services) {
    return (name, type) -> {
      TyType resolved = env.rules.Resolve(type, true);
      if (resolved instanceof TyNativeGlobalObject) return;
      if (resolved instanceof TyNativeFunctional) {
        variablesToWatch.addAll(((TyNativeFunctional) resolved).gatherDependencies());
        return;
      }
      if (resolved instanceof TyNativeService) {
        services.add(((TyNativeService) resolved).service.name.text);
        return;
      }
      if (!env.document.functionTypes.containsKey(name)) {
        variablesToWatch.add(name);
      }
    };
  }
}
