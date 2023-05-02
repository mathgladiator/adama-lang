/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives.functions;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.checking.properties.StorageTweak;

import java.util.ArrayList;

/**
 * a function overload instance is set of arguments bound to the same name such that the types of
 * parameters decide which function to use.
 */
public class FunctionOverloadInstance extends DocumentPosition {
  public final ArrayList<String> hiddenSuffixArgs;
  public final String javaFunction;
  public final boolean pure;
  public final TyType returnType;
  public final ArrayList<TyType> types;
  public final boolean castArgs;
  public final boolean aborts;

  public FunctionOverloadInstance(final String javaFunction, final TyType returnType, final ArrayList<TyType> types, final boolean pure, final boolean castArgs, final boolean aborts) {
    this.javaFunction = javaFunction;
    this.returnType = returnType;
    this.types = types;
    this.pure = pure;
    this.castArgs = castArgs;
    this.aborts = aborts;
    hiddenSuffixArgs = new ArrayList<>();
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
