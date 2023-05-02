/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.operands;

public enum PrefixMutateOp {
  BumpDown("--", ".bumpDownPre()", true), // ++
  BumpUp("++", ".bumpUpPre()", true), // --
  NegateBool("!", ".negate()", false), // !
  NegateNumber("-", ".negate()", false); // -

  public final String functionCall;
  public final String javaOp;
  public final boolean requiresAssignment;

  PrefixMutateOp(final String javaOp, final String functionCall, final boolean requiresAssignment) {
    this.javaOp = javaOp;
    this.functionCall = functionCall;
    this.requiresAssignment = requiresAssignment;
  }

  public static PrefixMutateOp fromText(final String txt) {
    for (final PrefixMutateOp op : PrefixMutateOp.values()) {
      if (op.javaOp.equals(txt)) {
        return op;
      }
    }
    return null;
  }
}
