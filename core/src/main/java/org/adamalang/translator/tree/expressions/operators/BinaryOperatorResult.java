/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.expressions.operators;

import org.adamalang.translator.tree.types.TyType;

/** <THING> $OP <THING> --> (Type, JavaCode) */
public class BinaryOperatorResult {
  public final TyType type;
  public final String javaPattern;
  public final boolean reverse;

  public BinaryOperatorResult(TyType type, String javaPattern, boolean reverse) {
    this.type = type;
    this.javaPattern = javaPattern;
    this.reverse = reverse;
  }
}
