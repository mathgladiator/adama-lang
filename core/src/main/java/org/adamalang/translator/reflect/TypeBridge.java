/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.reflect;

import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtList;
import org.adamalang.translator.tree.common.TokenizedItem;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeBoolean;
import org.adamalang.translator.tree.types.natives.TyNativeClient;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeList;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.natives.TyNativeString;

public class TypeBridge {
  public static TyType getAdamaType(final Class<?> x, final HiddenType ht) {
    if (int.class == x || Integer.class == x) {
      return new TyNativeInteger(null);
    } else if (Long.class == x || long.class == x) {
      return new TyNativeLong(null);
    } else if (Double.class == x || double.class == x) {
      return new TyNativeDouble(null);
    } else if (Boolean.class == x || boolean.class == x) {
      return new TyNativeBoolean(null);
    } else if (String.class == x) {
      return new TyNativeString(null);
    } else if (NtClient.class == x) {
      return new TyNativeClient(null);
    } else if (Void.class == x || void.class == x) {
      return null;
    } else if (NtList.class == x) {
      if (ht != null) { return new TyNativeList(null, new TokenizedItem<>(getAdamaType(ht.clazz(), null))); }
      throw new RuntimeException("NtList requires @HiddenType annotation because Java sucks:" + ht);
    }
    throw new RuntimeException("can't find:" + x.toString());
  }
}
