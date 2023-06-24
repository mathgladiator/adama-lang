/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.natives.functions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * a function overload instance is set of arguments bound to the same name such that the types of
 * parameters decide which function to use.
 */
public class FunctionOverloadInstance extends DocumentPosition {
  public final ArrayList<String> hiddenSuffixArgs;
  public final LinkedHashSet<String> dependencies;
  public final String javaFunction;
  public final boolean pure;
  public final TyType returnType;
  public final ArrayList<TyType> types;
  public final boolean castArgs;
  public final boolean aborts;
  public final boolean viewer;

  public FunctionOverloadInstance(final String javaFunction, final TyType returnType, final ArrayList<TyType> types, FunctionPaint paint) {
    this.javaFunction = javaFunction;
    this.returnType = returnType;
    this.types = types;
    this.pure = paint.pure;
    this.castArgs = paint.castArgs;
    this.aborts = paint.aborts;
    this.viewer = paint.viewer;
    this.hiddenSuffixArgs = new ArrayList<>();
    this.dependencies = new LinkedHashSet<>();
    if (this.viewer) {
      hiddenSuffixArgs.add("__viewer");
    }
  }

  public void dependOn(Collection<String> depends) {
    for (String depend : depends) {
      this.dependencies.add(depend);
    }
  }

  public static ArrayList<FunctionOverloadInstance> WRAP(final FunctionOverloadInstance foi) {
    final var list = new ArrayList<FunctionOverloadInstance>();
    list.add(foi);
    return list;
  }

  public int score(final Environment environment, final ArrayList<TyType> args) {
    var score = 0;
    if (args.size() != types.size()) {
      score = Math.abs(args.size() - types.size()) * 2;
    }
    for (var iter = 0; iter < Math.min(args.size(), types.size()); iter++) {
      if (!environment.rules.CanTypeAStoreTypeB(types.get(iter), args.get(iter), StorageTweak.FunctionScore, true)) {
        score++;
      }
      if (!environment.rules.CanTypeAStoreTypeB(args.get(iter), types.get(iter), StorageTweak.FunctionScore, true)) {
        score++;
      }
    }
    return score;
  }

  public void test(final DocumentPosition position, final Environment environment, final ArrayList<TyType> args) {
    if (args.size() != types.size()) {
      environment.document.createError(position, String.format("Function invoked with wrong number of arguments. Expected %d, got %d", types.size(), args.size()), "FunctionInvoke");
    }
    for (var iter = 0; iter < Math.min(args.size(), types.size()); iter++) {
      environment.rules.CanTypeAStoreTypeB(types.get(iter), args.get(iter), StorageTweak.None, false);
    }
  }

  public void testOverlap(final FunctionOverloadInstance other, final Environment environment) {
    if (types.size() != other.types.size()) {
      return;
    }
    var sameCount = 0;
    for (var iter = 0; iter < types.size(); iter++) {
      final var l2r = environment.rules.CanTypeAStoreTypeB(types.get(iter), other.types.get(iter), StorageTweak.None, true);
      final var r2l = environment.rules.CanTypeAStoreTypeB(other.types.get(iter), types.get(iter), StorageTweak.None, true);
      if (l2r && r2l) {
        sameCount++;
      }
    }
    if (sameCount == types.size()) {
      environment.document.createError(this, "Overloaded function has many identical calls", "FunctionOverlap");
    }
  }

  public void typing(final Environment environment) {
    if (returnType != null) {
      returnType.typing(environment);
    }
    for (final TyType argType : types) {
      argType.typing(environment);
    }
  }
}
