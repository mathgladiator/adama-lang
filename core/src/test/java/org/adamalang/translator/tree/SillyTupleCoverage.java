/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeTuple;
import org.junit.Assert;
import org.junit.Test;

public class SillyTupleCoverage {
  @Test
  public void coverage() {
    TyNativeTuple tuple = new TyNativeTuple(TypeBehavior.ReadOnlyNativeValue, Token.WRAP("tuple"), Token.WRAP("tuple"));
    tuple.add(Token.WRAP("HI"), new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, Token.WRAP("tuple"), Token.WRAP("int")));
    tuple.emit((token) -> {});
    try {
      tuple.getJavaBoxType(null);
      Assert.fail();
    } catch (UnsupportedOperationException uso) {
    }
    try {
      tuple.getJavaConcreteType(null);
      Assert.fail();
    } catch (UnsupportedOperationException uso) {
    }
    tuple.getAdamaType();
    tuple.typing(null);
    tuple.makeCopyWithNewPosition(tuple, TypeBehavior.ReadOnlyNativeValue);
    JsonStreamWriter writer = new JsonStreamWriter();
    tuple.writeTypeReflectionJson(writer);


  }
}
