/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.expressions.InjectExpression;
import org.adamalang.translator.tree.types.natives.TyNativeVoid;
import org.junit.Test;

public class InjectExpressionTests {
  @Test
  public void coverage() {
    final InjectExpression ie =
        new InjectExpression(new TyNativeVoid()) {
          @Override
          public void writeJava(final StringBuilder sb, final Environment environment) {}
        };
    ie.typing(null, null);
    ie.emit(null);
    ie.writeJava((StringBuilder) null, null);
  }
}
