/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives.functions;

public enum FunctionStyleJava {
  ExpressionThenArgs(false), //
  ExpressionThenNameWithArgs(true), //
  InjectName(true), InjectNameThenArgs(true), //
  InjectNameThenExpressionAndArgs(true), //
  None(false), //
  ;

  public final boolean useOnlyExpressionInLookup;

  FunctionStyleJava(final boolean useOnlyExpressionInLookup) {
    this.useOnlyExpressionInLookup = useOnlyExpressionInLookup;
  }
}
