/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.reflect;

import org.adamalang.runtime.natives.*;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.*;

/** convert a known java type into an Adama type */
public class TypeBridge {

  public static TyType getAdamaSubType(String core, final Class<?>[] hiddenTypes) {
    if (hiddenTypes == null || hiddenTypes.length == 0) {
      throw new RuntimeException(core + " requires @HiddenType/@HiddenTypes annotation because Java sucks");
    }
    Class<?> head = hiddenTypes[0];
    Class<?>[] tail = new Class[hiddenTypes.length - 1];
    for (int k = 0; k < tail.length; k++) {
      tail[k] = hiddenTypes[k + 1];
    }
    return getAdamaType(head, tail);
  }

  public static TyType getAdamaType(final Class<?> x, final Class<?>[] hiddenTypes) {
    if (int.class == x || Integer.class == x) {
      return new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Long.class == x || long.class == x) {
      return new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Double.class == x || double.class == x) {
      return new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Boolean.class == x || boolean.class == x) {
      return new TyNativeBoolean(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (String.class == x) {
      return new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (NtClient.class == x) {
      return new TyNativeClient(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (NtComplex.class == x) {
      return new TyNativeComplex(TypeBehavior.ReadOnlyNativeValue, null, null);
    } else if (Void.class == x || void.class == x) {
      return null;
    } else if (NtDynamic.class == x) {
      return new TyNativeDynamic(TypeBehavior.ReadWriteNative, null, null);
    } else if (NtComplex.class == x) {
      return new TyNativeComplex(TypeBehavior.ReadWriteNative, null, null);
    } else if (NtList.class == x) {
      TyType subType = getAdamaSubType("NtList<>", hiddenTypes);
      return new TyNativeList(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(subType));
    } else if (NtMaybe.class == x) {
      TyType subType = getAdamaSubType("NtMaybe<>", hiddenTypes);
      return new TyNativeMaybe(TypeBehavior.ReadOnlyNativeValue, null, null, new TokenizedItem<>(subType));
    }
    throw new RuntimeException("can't find:" + x.toString());
  }
}
