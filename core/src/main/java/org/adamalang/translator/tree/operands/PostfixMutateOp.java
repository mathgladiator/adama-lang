/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
