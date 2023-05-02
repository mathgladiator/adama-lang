/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.env;

import org.adamalang.runtime.stdlib.*;
import org.adamalang.translator.reflect.GlobalFactory;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

/** a pool of global objects like Math, Random, String */
public class GlobalObjectPool {
  protected final HashMap<String, HashMap<String, TyNativeFunctional>> extensions;
  private final HashMap<String, TyNativeGlobalObject> globalObjects;

  private GlobalObjectPool() {
    globalObjects = new HashMap<>();
    extensions = new HashMap<>();
  }

  public static GlobalObjectPool createPoolWithStdLib() {
    final TyNativeString tyStr = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null);
    final TyNativeInteger tyInt = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null);
    final TyNativeDouble tyDbl = new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null);
    final TyNativeLong tyLng = new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null);
    final TyNativeBoolean tyBool = new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null);

    final var pool = new GlobalObjectPool();
    pool.add(GlobalFactory.makeGlobal("String", LibString.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Math", LibMath.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Adama", LibAdama.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Statistics", LibStatistics.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Date", LibDate.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Client", LibPrincipal.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Dynamic", LibDynamic.class, pool.extensions));
    final var document = new TyNativeGlobalObject("Document", null, false);
    document.functions.put("destroy", generateInternalDocumentFunction("__destroyDocument", new TyNativeVoid()));
    document.functions.put("rewind", generateInternalDocumentFunction("__rewindDocument", tyInt, new TyNativeVoid()));
    document.functions.put("key", generateInternalDocumentFunction("__getKey", tyStr));
    document.functions.put("space", generateInternalDocumentFunction("__getSpace", tyStr));
    document.functions.put("seq", generateInternalDocumentFunction("__getSeq", tyInt));
    pool.add(document);
    final var random = new TyNativeGlobalObject("Random", null, false);
    random.functions.put("genBoundInt", generateInternalDocumentFunction("__randomBoundInt", tyInt, tyInt));
    random.functions.put("genInt", generateInternalDocumentFunction("__randomInt", tyInt));
    random.functions.put("genDouble", generateInternalDocumentFunction("__randomDouble", tyDbl));
    random.functions.put("getDoubleGaussian", generateInternalDocumentFunction("__randomGaussian", tyDbl));
    random.functions.put("genLong", generateInternalDocumentFunction("__randomLong", tyLng));
    pool.add(random);
    final var time = new TyNativeGlobalObject("Time", null, false);
    time.functions.put("now", generateInternalDocumentFunction("__timeNow", tyLng));
    time.functions.put("today", generateInternalDocumentFunction("__dateOfToday", new TyNativeDate(TypeBehavior.ReadOnlyNativeValue, null, null)));
    time.functions.put("datetime", generateInternalDocumentFunction("__datetimeNow", new TyNativeDateTime(TypeBehavior.ReadOnlyNativeValue, null, null)));
    time.functions.put("zone", generateInternalDocumentFunction("__timeZone", tyStr));
    time.functions.put("setZone", generateInternalDocumentFunction("__setTimeZone", tyStr, tyBool));
    time.setParentOverride((GlobalFactory.makeGlobal("LibTime", LibTime.class, pool.extensions)));
    pool.add(time);
    return pool;
  }

  public void add(final TyNativeGlobalObject globalObject) {
    globalObjects.put(globalObject.globalName, globalObject);
  }

  private static TyNativeFunctional generateInternalDocumentFunction(final String name, final TyType returnType) {
    final var overloads = new ArrayList<FunctionOverloadInstance>();
    final var args = new ArrayList<TyType>();
    overloads.add(new FunctionOverloadInstance(name, returnType, args, true, false, false));
    return new TyNativeFunctional(name, overloads, FunctionStyleJava.InjectNameThenArgs);
  }

  private static TyNativeFunctional generateInternalDocumentFunction(final String name, final TyType arg, final TyType returnType) {
    final var overloads = new ArrayList<FunctionOverloadInstance>();
    final var args = new ArrayList<TyType>();
    args.add(arg);
    overloads.add(new FunctionOverloadInstance(name, returnType, args, true, false, false));
    return new TyNativeFunctional(name, overloads, FunctionStyleJava.InjectNameThenArgs);
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
