/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.types.natives;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.common.DocumentPosition;
import org.adamalang.translator.tree.types.ReflectionSource;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.junit.Assert;
import org.junit.Test;

public class TyNativeLazyWrapTests {
  @Test
  public void coverage() {
    TyNativeLazyWrap wrap = new TyNativeLazyWrap(new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("init")));
    wrap.emit((t) -> {});
    Assert.assertEquals("int", wrap.getAdamaType());
    Assert.assertEquals("int", wrap.getJavaConcreteType(null));
    Assert.assertEquals("Integer", wrap.getJavaBoxType(null));
    wrap.makeCopyWithNewPositionInternal(DocumentPosition.ZERO, TypeBehavior.ReadOnlyGetNativeValue);
    wrap.typing(null);
    wrap.writeTypeReflectionJson(new JsonStreamWriter(), ReflectionSource.Root);
    wrap.typeAfterGet(null);
  }
}
