/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
