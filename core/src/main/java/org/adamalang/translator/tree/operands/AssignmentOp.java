/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.operands;

/** defines what is possible during assignment */
public enum AssignmentOp {
  AddTo("+=", ".opAddTo"), //
  DivideBy("/=", ".opDivBy"), //
  IngestFrom("<-", "/* N/A */"), //
  ModBy("%=", ".opModBy"), //
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
