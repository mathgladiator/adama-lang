/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
  AssignmentSubtract("-=", true), //
  AssignmentMultiply("*=", true), //
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
