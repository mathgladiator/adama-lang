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

/** defines what is possible during assignment */
public enum AssignmentOp {
  AddTo("+=", ".opAddTo"), //
  IngestFrom("<-", "/* N/A */"), //
  MultiplyBy("*=", ".opMultBy"), //
  Set("=", ".set"), //
  SubtractFrom("-=", ".opSubFrom") //
  ;

  public static AssignmentOp fromText(final String txt) {
    for (final AssignmentOp op : AssignmentOp.values()) {
      if (op.js.equals(txt)) { return op; }
    }
    return null;
  }

  public final String js;
  public final String notNative;

  AssignmentOp(final String js, final String notNative) {
    this.js = js;
    this.notNative = notNative;
  }
}
