package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.tree.types.TyType;

/** <THING> $OP <THING> --> (Type, JavaCode) */
public class BinaryOperatorResult {
  public final TyType type;
  public final String javaPattern;
  public final boolean reverse;

  public BinaryOperatorResult(TyType type, String javaPattern, boolean reverse) {
    this.type = type;
    this.javaPattern = javaPattern;
    this.reverse = reverse;
  }
}
