/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
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
  Add("+", false), //
  Divide("/", false), //
  Equal("==", false), //
  GreaterThan(">", false), //
  GreaterThanOrEqual(">=", false), //
  LessThan("<", false), //
  LessThanOrEqual("<=", false), //
  LogicalXor("^^", false), //
  LogicalAnd("&&", false), //
  LogicalOr("||", false), //
  Mod("%", false), //
  Multiply("*", false), //
  NotEqual("!=", false), //
  Subtract("-", false), //
  AssignmentAdd("+=", true), //
  ; //

  public final String javaOp;
  public final boolean leftAssignment;

  BinaryOp(final String js, boolean leftAssignment) {
    this.javaOp = js;
    this.leftAssignment = leftAssignment;
  }

  public static BinaryOp fromText(final String txt) {
    for (final BinaryOp op : BinaryOp.values()) {
      if (op.javaOp.equals(txt)) {
        return op;
      }
    }
    return null;
  }
}
