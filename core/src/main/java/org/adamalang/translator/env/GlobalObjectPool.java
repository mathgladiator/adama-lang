/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.env;

import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.stdlib.*;
import org.adamalang.translator.reflect.GlobalFactory;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionPaint;
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
    final TyNativePrincipal tyPrincipal = new TyNativePrincipal(TypeBehavior.ReadOnlyNativeValue, null, null);
    final TyNativeVoid tyVoid = new TyNativeVoid();
    final TyNativeDynamic tyDyn = new TyNativeDynamic(TypeBehavior.ReadOnlyNativeValue, null, null);

    final var pool = new GlobalObjectPool();
    pool.add(GlobalFactory.makeGlobal("String", LibString.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Math", LibMath.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Adama", LibAdama.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Statistics", LibStatistics.class, pool.extensions));
    pool.add(GlobalFactory.makeGlobal("Date", LibDate.class, pool.extensions));

    final var client = new TyNativeGlobalObject("Client", null, false);
    client.setParentOverride(GlobalFactory.makeGlobal("Client", LibPrincipal.class, pool.extensions));
    client.functions.put("principalOf", generateInternalDocumentFunction("__principalOf", tyStr, tyPrincipal, "principalOf", pool.extensions));
    client.functions.put("isFromDocument", generateInternalDocumentFunction("__isFromDocument", tyPrincipal, tyBool, "isFromDocument", pool.extensions));
    pool.add(client);

    pool.add(GlobalFactory.makeGlobal("Dynamic", LibDynamic.class, pool.extensions));
    final var document = new TyNativeGlobalObject("Document", null, false);
    document.functions.put("destroy", generateInternalDocumentFunction("__destroyDocument", tyVoid));
    document.functions.put("rewind", generateInternalDocumentFunction("__rewindDocument", tyInt, tyVoid, null, null));
    document.functions.put("key", generateInternalDocumentFunction("__getKey", tyStr));
    document.functions.put("space", generateInternalDocumentFunction("__getSpace", tyStr));
    document.functions.put("seq", generateInternalDocumentFunction("__getSeq", tyInt));
    pool.add(document);

    final var viewer = new TyNativeGlobalObject("ViewState", null, false);
    viewer.functions.put("merge", generateInternalDocumentFunction("__mergeViewState", tyDyn, tyBool, null, null));
    viewer.functions.put("goto", generateInternalDocumentFunction("__gotoViewState", tyStr, tyBool, null, null));
    pool.add(viewer);

    final var random = new TyNativeGlobalObject("Random", null, false);
    random.functions.put("genBoundInt", generateInternalDocumentFunction("__randomBoundInt", tyInt, tyInt, null, null));
    random.functions.put("genInt", generateInternalDocumentFunction("__randomInt", tyInt));
    random.functions.put("genDouble", generateInternalDocumentFunction("__randomDouble", tyDbl));
    random.functions.put("getDoubleGaussian", generateInternalDocumentFunction("__randomGaussian", tyDbl));
    random.functions.put("genLong", generateInternalDocumentFunction("__randomLong", tyLng));
    pool.add(random);
    final var time = new TyNativeGlobalObject("Time", null, false);
    time.functions.put("now", subscribe("__time", generateInternalDocumentFunction("__timeNow", tyLng)));
    time.functions.put("today", subscribe("__today", generateInternalDocumentFunction("__dateOfToday", new TyNativeDate(TypeBehavior.ReadOnlyNativeValue, null, null))));
    time.functions.put("datetime", subscribe("__time", generateInternalDocumentFunction("__datetimeNow", new TyNativeDateTime(TypeBehavior.ReadOnlyNativeValue, null, null))));
    time.functions.put("zone", generateInternalDocumentFunction("__timeZone", tyStr));
    time.functions.put("setZone", generateInternalDocumentFunction("__setTimeZone", tyStr, tyBool, null, null));
    time.setParentOverride((GlobalFactory.makeGlobal("LibTime", LibTime.class, pool.extensions)));
    pool.add(time);
    return pool;
  }

  /** common policy for watch to ignore various names and types */
  public static boolean ignoreCapture(String name, TyType ty) {
    if (ty instanceof TyNativeGlobalObject || ty instanceof TyNativeFunctional) {
      return true;
    }
    switch (name) {
      case "__time":
      case "__today":
        return true;
      default:
        return false;
    }
  }

  private static TyNativeFunctional subscribe(String depend, TyNativeFunctional func) {
    for (FunctionOverloadInstance foi : func.overloads) {
      foi.dependencies.add(depend);
    }
    return func;
  }

  public void add(final TyNativeGlobalObject globalObject) {
    globalObjects.put(globalObject.globalName, globalObject);
  }

  private static TyNativeFunctional generateInternalDocumentFunction(final String name, final TyType returnType) {
    final var overloads = new ArrayList<FunctionOverloadInstance>();
    final var args = new ArrayList<TyType>();
    overloads.add(new FunctionOverloadInstance(name, returnType, args, FunctionPaint.READONLY_NORMAL));
    return new TyNativeFunctional(name, overloads, FunctionStyleJava.InjectNameThenArgs);
  }

  private static TyNativeFunctional generateInternalDocumentFunction(final String name, final TyType arg, final TyType returnType, String adamaName, final HashMap<String, HashMap<String, TyNativeFunctional>> extensions) {
    final var overloads = new ArrayList<FunctionOverloadInstance>();
    final var args = new ArrayList<TyType>();
    args.add(arg);
    FunctionOverloadInstance foi = new FunctionOverloadInstance(name, returnType, args, FunctionPaint.READONLY_NORMAL);
    overloads.add(foi);
    if (extensions != null) {
      HashMap<String, ArrayList<FunctionOverloadInstance>> byFirstParameterType = new HashMap<>();
      GlobalFactory.prepareForExtension(foi, byFirstParameterType);
      GlobalFactory.injectExtension(adamaName, byFirstParameterType, extensions);
    }
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
