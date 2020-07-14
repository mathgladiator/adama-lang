/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.operands;

public enum PrefixMutateOp {
  BumpDown("--", ".bumpDownPre()", true), // ++
  BumpUp("++", ".bumpUpPre()", true), // --
  NegateBool("!", ".negate()", false), // !
  NegateNumber("-", ".negate()", false); // -

  public static PrefixMutateOp fromText(final String txt) {
    for (final PrefixMutateOp op : PrefixMutateOp.values()) {
      if (op.javaOp.equals(txt)) { return op; }
    }
    return null;
  }

  public final String functionCall;
  public final String javaOp;
  public final boolean requiresAssignment;

  PrefixMutateOp(final String javaOp, final String functionCall, final boolean requiresAssignment) {
    this.javaOp = javaOp;
    this.functionCall = functionCall;
    this.requiresAssignment = requiresAssignment;
  }
}
