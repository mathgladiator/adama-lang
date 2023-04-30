/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.reflect;

import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;
import org.adamalang.translator.tree.types.natives.TyNativeGlobalObject;
import org.adamalang.translator.tree.types.natives.functions.FunctionOverloadInstance;
import org.adamalang.translator.tree.types.natives.functions.FunctionStyleJava;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/** has the job of turning static methods on a class into a format that can be consumed by Adama */
public class GlobalFactory {

  /** get the public static methods of the given class */
  public static String[] publicStaticMethodsOf(final Class<?> clazz) {
    final var methods = new TreeSet<String>();
    for (final Method method : clazz.getMethods()) {
      final var isStatic = Modifier.isStatic(method.getModifiers());
      final var isPublic = Modifier.isPublic(method.getModifiers());
      if (method.getAnnotation(Skip.class) != null) {
        continue;
      }
      if (isPublic && isStatic) {
        final var nameToUse = getMethodName(method);
        methods.add(nameToUse);
      }
    }
    for (final Field field : clazz.getFields()) {
      if (field.getAnnotation(Skip.class) != null) {
        continue;
      }
      if (Modifier.isStatic(field.getModifiers())) {
        methods.add(field.getName());
      }
    }
    return methods.toArray(new String[methods.size()]);
  }

  /** we iterate all public static methods within a class */
  public static TyNativeGlobalObject makeGlobal(final String name, final Class<?> clazz, HashMap<String, HashMap<String, TyNativeFunctional>> extensions) {
    return makeGlobalExplicit(name, clazz, extensions, false, publicStaticMethodsOf(clazz));
  }

  private static String getMethodName(final Method method) {
    for (final Annotation at : method.getAnnotations()) {
      if (at instanceof UseName) {
        return ((UseName) at).name();
      }
    }
    return method.getName();
  }

  /** make a global object with the given type */
  public static TyNativeGlobalObject makeGlobalExplicit(final String name, final Class<?> clazz, HashMap<String, HashMap<String, TyNativeFunctional>> extensions, boolean forceException, final String... methodsToGet) {
    final var object = new TyNativeGlobalObject(name, clazz.getPackageName() + "." + clazz.getSimpleName(), true);
    mergeInto(object, clazz, extensions, forceException, methodsToGet);
    return object;
  }

  public static void mergeInto(final TyNativeGlobalObject object, final Class<?> clazz, HashMap<String, HashMap<String, TyNativeFunctional>> extensions, boolean forceException, final String... methodsToGet) {
    final var index = indexMethods(clazz, methodsToGet);
    for (final Map.Entry<String, ArrayList<Method>> entry : index.entrySet()) { // for each method
      final var overloads = new ArrayList<FunctionOverloadInstance>();
      HashMap<String, ArrayList<FunctionOverloadInstance>> byFirstParameterType = new HashMap<>();
      for (final Method method : entry.getValue()) {
        final var fo = convertMethodToFunctionOverload(clazz, method);
        if (fo != null) {
          overloads.add(fo);
          if (isExtension(method) || forceException) {
            String key = null;
            ArrayList<TyType> affix = new ArrayList<>();
            boolean first = true;
            for (TyType argType : fo.types) {
              if (first) {
                key = argType.getAdamaType();
                first = false;
              } else {
                affix.add(argType);
              }
            }
            if (key != null) {
              ArrayList<FunctionOverloadInstance> newOverloads = byFirstParameterType.get(key);
              if (newOverloads == null) {
                newOverloads = new ArrayList<>();
                byFirstParameterType.put(key, newOverloads);
              }
              newOverloads.add(new FunctionOverloadInstance(fo.javaFunction, fo.returnType, affix, fo.pure, false, false));
            }
          }
        }
      }

      if (overloads.size() > 0) {
        object.functions.put(entry.getKey(), new TyNativeFunctional(entry.getKey(), overloads, FunctionStyleJava.InjectNameThenArgs));
        for (Map.Entry<String, ArrayList<FunctionOverloadInstance>> firstParamKey : byFirstParameterType.entrySet()) {
          HashMap<String, TyNativeFunctional> extensionByFirstParam = extensions.get(firstParamKey.getKey());
          if (extensionByFirstParam == null) {
            extensionByFirstParam = new HashMap<>();
            extensions.put(firstParamKey.getKey(), extensionByFirstParam);
          }
          extensionByFirstParam.put(entry.getKey(), new TyNativeFunctional(entry.getKey(), firstParamKey.getValue(), FunctionStyleJava.InjectNameThenExpressionAndArgs));
        }
      } else {
        try {
          final var fld = clazz.getField(entry.getKey());
          overloads.add(new FunctionOverloadInstance(clazz.getSimpleName() + "." + entry.getKey(), TypeBridge.getAdamaType(fld.getType(), null), new ArrayList<>(), true, false, false));
          object.functions.put(entry.getKey(), new TyNativeFunctional(entry.getKey(), overloads, FunctionStyleJava.InjectName));
        } catch (final NoSuchFieldException nsfe) {
        }
      }
    }
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

  public static FunctionOverloadInstance convertMethodToFunctionOverload(final Class<?> clazz, final Method method) {
    final var args = new ArrayList<TyType>();
    final var params = method.getParameterTypes();
    for (var k = 0; k < params.length; k++) {
      if (shouldDeny(params[k])) {
        return null;
      }
      args.add(TypeBridge.getAdamaType(params[k], extractHiddenTypes(method.getParameterAnnotations()[k])));
    }
    return new FunctionOverloadInstance(clazz.getSimpleName() + "." + method.getName(), TypeBridge.getAdamaType(method.getReturnType(), extractHiddenTypes(method.getAnnotations())), args, true, false, false);
  }

  private static boolean isExtension(final Method method) {
    for (final Annotation at : method.getAnnotations()) {
      if (at instanceof Extension) {
        return true;
      }
    }
    return false;
  }

  public static boolean shouldDeny(final Class<?> x) {
    return float.class == x || Float.class == x;
  }

  private static Class<?>[] extractHiddenTypes(final Annotation[] annotations) {
    for (final Annotation at : annotations) {
      if (at instanceof HiddenType) {
        return new Class<?>[]{((HiddenType) at).clazz()};
      }
      if (at instanceof HiddenTypes2) {
        return new Class<?>[]{((HiddenTypes2) at).class1(), ((HiddenTypes2) at).class2()};
      }
    }
    return null;
  }
}
