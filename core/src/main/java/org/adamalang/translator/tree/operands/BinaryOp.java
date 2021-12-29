/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
