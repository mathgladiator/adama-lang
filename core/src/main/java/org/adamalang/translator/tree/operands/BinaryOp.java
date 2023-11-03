/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
  Search("=?", false), //
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
  Inside("inside", false), //
  NotInside("outside", false), //
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
