/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
