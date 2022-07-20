/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
