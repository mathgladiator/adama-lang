/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.operands;

public enum PostfixMutateOp {
  BumpDown("--", ".bumpDownPost()"), // ++
  BumpUp("++", ".bumpUpPost()"); // --

  public static PostfixMutateOp fromText(final String txt) {
    for (final PostfixMutateOp op : PostfixMutateOp.values()) {
      if (op.javaOp.equals(txt)) { return op; }
    }
    return null;
  }

  public final String functionCall;
  public final String javaOp;

  PostfixMutateOp(final String javaOp, final String functionCall) {
    this.javaOp = javaOp;
    this.functionCall = functionCall;
  }
}
