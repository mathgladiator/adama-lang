/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

/** has the job of turning static methods on a class into a format that can be
 * consumed by Adama */
public class GlobalFactory {
  public static FunctionOverloadInstance convertMethodToFunctionOverload(final Class<?> clazz, final Method method) {
    final var args = new ArrayList<TyType>();
    final var params = method.getParameterTypes();
    for (var k = 0; k < params.length; k++) {
      if (shouldDeny(params[k])) { return null; }
      args.add(TypeBridge.getAdamaType(params[k], extractHiddenType(method.getParameterAnnotations()[k])));
    }
    return new FunctionOverloadInstance(clazz.getSimpleName() + "." + method.getName(), TypeBridge.getAdamaType(method.getReturnType(), extractHiddenType(method.getAnnotations())), args, true);
  }

  private static HiddenType extractHiddenType(final Annotation[] annotations) {
    for (final Annotation at : annotations) {
      if (at instanceof HiddenType) { return (HiddenType) at; }
    }
    return null;
  }

  private static String getMethodName(final Method method) {
    for (final Annotation at : method.getAnnotations()) {
      if (at instanceof UseName) { return ((UseName) at).name(); }
    }
    return method.getName();
  }

  private static TreeMap<String, ArrayList<Method>> indexMethods(final Class<?> clazz, final String[] methodsToGet) {
    final var indexedMethods = new TreeMap<String, ArrayList<Method>>();
    final var methods = new TreeSet<String>();
    for (final String method : methodsToGet) {
      methods.add(method);
      indexedMethods.put(method, new ArrayList<>());
    }
    for (final Method method : clazz.getMethods()) {
      final var isStatic = Modifier.isStatic(method.getModifiers());
      final var isPublic = Modifier.isPublic(method.getModifiers());
      final var nameToUse = getMethodName(method);
      if (isPublic && isStatic) {
        final var arr = indexedMethods.get(nameToUse);
        if (arr != null) {
          arr.add(method);
        }
      }
    }
    return indexedMethods;
  }

  /** we iterate all public static methods within a class */
  public static TyNativeGlobalObject makeGlobal(final String name, final Class<?> clazz) {
    final var methods = new TreeSet<String>();
    for (final Method method : clazz.getMethods()) {
      final var isStatic = Modifier.isStatic(method.getModifiers());
      final var isPublic = Modifier.isPublic(method.getModifiers());
      if (isPublic && isStatic) {
        final var nameToUse = getMethodName(method);
        methods.add(nameToUse);
      }
    }
    return makeGlobalExplicit(name, clazz, methods.toArray(new String[methods.size()]));
  }

  public static TyNativeGlobalObject makeGlobalExplicit(final String name, final Class<?> clazz, final String... methodsToGet) {
    final var object = new TyNativeGlobalObject(name, clazz.getPackageName() + "." + clazz.getSimpleName());
    mergeInto(object, clazz, methodsToGet);
    return object;
  }

  public static void mergeInto(final TyNativeGlobalObject object, final Class<?> clazz, final String... methodsToGet) {
    final var index = indexMethods(clazz, methodsToGet);
    for (final Map.Entry<String, ArrayList<Method>> entry : index.entrySet()) {
      final var overloads = new ArrayList<FunctionOverloadInstance>();
      for (final Method method : entry.getValue()) {
        final var fo = convertMethodToFunctionOverload(clazz, method);
        if (fo != null) {
          overloads.add(fo);
        }
      }
      if (overloads.size() > 0) {
        object.functions.put(entry.getKey(), new TyNativeFunctional(entry.getKey(), overloads, FunctionStyleJava.InjectNameThenArgs));
      } else {
        try {
          final var fld = clazz.getField(entry.getKey());
          overloads.add(new FunctionOverloadInstance(clazz.getSimpleName() + "." + entry.getKey(), TypeBridge.getAdamaType(fld.getType(), null), new ArrayList<>(), true));
          object.functions.put(entry.getKey(), new TyNativeFunctional(entry.getKey(), overloads, FunctionStyleJava.InjectName));
        } catch (final NoSuchFieldException nsfe) {}
      }
    }
  }

  public static boolean shouldDeny(final Class<?> x) {
    if (float.class == x || Float.class == x) { return true; }
    return false;
  }
}
