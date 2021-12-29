/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import org.adamalang.runtime.stdlib.LibMath;
import org.adamalang.runtime.stdlib.LibStatistics;
import org.adamalang.runtime.stdlib.LibString;
import org.adamalang.translator.reflect.GlobalFactory;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

/** a pool of global objects like Math, Random, String */
public class GlobalObjectPool {
  public static GlobalObjectPool createPoolWithStdLib() {
    final var pool = new GlobalObjectPool();
    pool.add(GlobalFactory.makeGlobal("String", LibString.class, pool.extensions));
    final var mathlib = GlobalFactory.makeGlobalExplicit("Math", Math.class, pool.extensions, true,"min", "max", "ceil", "floor", "sin", "cos", "tan", "abs", "asin", "acos", "atan", "toRadians", "toDegrees", "sinh", "cosh", "tanh", "atan2", "hypot",
        "exp", "log", "log10", "pow", "cbrt", "floorDiv", "floorMod", "IEEEremainder", "expm1", "log1p", "signum", "ulp", "fma", "copySign", "getExponent", "powerOfTwo", "E", "PI");
    GlobalFactory.mergeInto(mathlib, LibMath.class, pool.extensions, true, "near", "sqrt", "SQRT2", "round", "roundTo", "conj", "len");
    pool.add(mathlib);
    pool.add(GlobalFactory.makeGlobal("Statistics", LibStatistics.class, pool.extensions));
    final var document = new TyNativeGlobalObject("Document", null, false);
    document.functions.put("destroy", generateInternalDocumentFunction("__destroyDocument", new TyNativeVoid()));
    document.functions.put("rewind", generateInternalDocumentFunction("__rewindDocument", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), new TyNativeVoid()));
    pool.add(document);
    final var random = new TyNativeGlobalObject("Random", null, false);
    random.functions.put("genBoundInt", generateInternalDocumentFunction("__randomBoundInt", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null), new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("genInt", generateInternalDocumentFunction("__randomInt", new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("genDouble", generateInternalDocumentFunction("__randomDouble", new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("getDoubleGaussian", generateInternalDocumentFunction("__randomGaussian", new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null)));
    random.functions.put("genLong", generateInternalDocumentFunction("__randomLong", new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null)));
    pool.add(random);
    final var time = new TyNativeGlobalObject("Time", null, false);
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
  protected final HashMap<String, HashMap<String, TyNativeFunctional>> extensions;

  private GlobalObjectPool() {
    globalObjects = new HashMap<>();
    extensions = new HashMap<>();
  }

  public void add(final TyNativeGlobalObject globalObject) {
    globalObjects.put(globalObject.globalName, globalObject);
  }

  public TyNativeFunctional findExtension(TyType type, String name) {
    HashMap<String, TyNativeFunctional> extensionsOnType = extensions.get(type.getAdamaType());
    if (extensionsOnType != null) {
      return extensionsOnType.get(name);
    }
    return null;
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
