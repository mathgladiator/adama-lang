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
