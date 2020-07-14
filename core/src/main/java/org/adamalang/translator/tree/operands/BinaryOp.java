/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.operands;

/** defines a binary operation (A op B) */
public enum BinaryOp {
  Add("+"), // *
  Divide("/"), // /
  Equal("=="), // ^
  GreaterThan(">"), // +
  GreaterThanOrEqual(">="), // -
  LessThan("<"), // <
  LessThanOrEqual("<="), // >
  LogicalAnd("&&"), // <=
  LogicalOr("||"), // >=
  Mod("%"), // ==
  Multiply("*"), // !=
  NotEqual("!="), // &&
  Subtract("-"); // ||

  public static BinaryOp fromText(final String txt) {
    for (final BinaryOp op : BinaryOp.values()) {
      if (op.javaOp.equals(txt)) { return op; }
    }
    return null;
  }

  public final String javaOp;

  BinaryOp(final String js) {
    javaOp = js;
  }
}
