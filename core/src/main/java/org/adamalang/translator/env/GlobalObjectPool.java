/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import org.adamalang.runtime.stdlib.LibMath;
import org.adamalang.runtime.stdlib.LibSecure;
import org.adamalang.runtime.stdlib.LibStatistics;
import org.adamalang.runtime.stdlib.LibString;
import org.adamalang.translator.reflect.GlobalFactory;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

/** a pool of global objects like Math, Random, String */
public class GlobalObjectPool {
  public static GlobalObjectPool createPoolWithStdLib() {
    final var pool = new GlobalObjectPool();
    pool.add(GlobalFactory.makeGlobal("String", LibString.class));
    final var mathlib = GlobalFactory.makeGlobalExplicit("Math", Math.class, "min", "max", "ceil", "floor", "sin", "cos", "tan", "abs", "round", "asin", "acos", "atan", "toRadians", "toDegrees", "sinh", "cosh", "tanh", "atan2", "hypot",
        "exp", "log", "log10", "pow", "sqrt", "cbrt", "floorDiv", "floorMod", "IEEEremainder", "expm1", "log1p", "signum", "ulp", "fma", "copySign", "getExponent", "powerOfTwo", "E", "PI");
    GlobalFactory.mergeInto(mathlib, LibMath.class, "near", "SQRT2");
    pool.add(mathlib);
    pool.add(GlobalFactory.makeGlobal("Secure", LibSecure.class));
    pool.add(GlobalFactory.makeGlobal("Statistics", LibStatistics.class));
    final var random = new TyNativeGlobalObject("Random", null);
    random.functions.put("genBoundInt", generateInternalDocumentFunction("__randomBoundInt", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("genInt", generateInternalDocumentFunction("__randomInt", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("genDouble", generateInternalDocumentFunction("__randomDouble", new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("getDoubleGaussian", generateInternalDocumentFunction("__randomGaussian", new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("genLong", generateInternalDocumentFunction("__randomLong", new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null)));
    pool.add(random);
    final var time = new TyNativeGlobalObject("Time", null);
    time.functions.put("now", generateInternalDocumentFunction("__timeNow", new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null)));
    pool.add(time);
    return pool;
  }

  private static TyNativeFunctional generateInternalDocumentFunction(final String name, final TyType returnType) {
    final var overloads = new ArrayList<FunctionOverloadInstance>();
    final var args = new ArrayList<TyType>();
    overloads.add(new FunctionOverloadInstance(name, returnType, args, true));
    return new TyNativeFunctional(name, overloads, FunctionStyleJava.InjectNameThenArgs);
  }

  private static TyNativeFunctional generateInternalDocumentFunction(final String name, final TyType arg, final TyType returnType) {
    final var overloads = new ArrayList<FunctionOverloadInstance>();
    final var args = new ArrayList<TyType>();
    args.add(arg);
    overloads.add(new FunctionOverloadInstance(name, returnType, args, true));
    return new TyNativeFunctional(name, overloads, FunctionStyleJava.InjectNameThenArgs);
  }

  private final HashMap<String, TyNativeGlobalObject> globalObjects;

  private GlobalObjectPool() {
    globalObjects = new HashMap<>();
  }

  public void add(final TyNativeGlobalObject globalObject) {
    globalObjects.put(globalObject.globalName, globalObject);
  }

  public TyNativeGlobalObject get(final String name) {
    return globalObjects.get(name);
  }

  public TreeSet<String> imports() {
    final var x = new TreeSet<String>();
    for (final TyNativeGlobalObject o : globalObjects.values()) {
      if (o.importPackage != null) {
        x.add(o.importPackage);
      }
    }
    return x;
  }
}
