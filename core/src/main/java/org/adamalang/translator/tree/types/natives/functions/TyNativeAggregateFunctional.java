/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives.functions;

import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;

public class TyNativeAggregateFunctional extends TyNativeFunctional {
  public final TyType typeBase;

  public TyNativeAggregateFunctional(final TyType typeBase, final TyNativeFunctional type) {
    super(type.name, type.overloads, type.style);
    this.typeBase = typeBase;
  }
}
