/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.tree;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.expressions.InjectExpression;
import org.adamalang.translator.tree.types.natives.TyNativeVoid;
import org.junit.Test;

public class InjectExpressionTests {
  @Test
  public void coverage() {
    final InjectExpression ie = new InjectExpression(new TyNativeVoid()) {
      @Override
      public void writeJava(final StringBuilder sb, final Environment environment) {
      }
    };
    ie.typing(null, null);
    ie.emit(null);
    ie.writeJava((StringBuilder) null, null);
  }
}
