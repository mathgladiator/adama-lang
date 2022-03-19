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
