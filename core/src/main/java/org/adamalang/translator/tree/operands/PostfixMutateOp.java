/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.tree.operands;

public enum PostfixMutateOp {
  BumpDown("--", ".bumpDownPost()"), // ++
  BumpUp("++", ".bumpUpPost()"); // --

  public final String functionCall;
  public final String javaOp;

  PostfixMutateOp(final String javaOp, final String functionCall) {
    this.javaOp = javaOp;
    this.functionCall = functionCall;
  }

  public static PostfixMutateOp fromText(final String txt) {
    for (final PostfixMutateOp op : PostfixMutateOp.values()) {
      if (op.javaOp.equals(txt)) {
        return op;
      }
    }
    return null;
  }
}
