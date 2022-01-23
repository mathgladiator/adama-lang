/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
import org.adamalang.translator.tree.types.TypeBehavior;
import org.adamalang.translator.tree.types.checking.properties.CanMathResult;
import org.adamalang.translator.tree.types.natives.TyNativeDouble;
import org.adamalang.translator.tree.types.natives.TyNativeInteger;
import org.adamalang.translator.tree.types.natives.TyNativeLong;
import org.adamalang.translator.tree.types.natives.TyNativeString;

public class RuleSetMath {
  public static TyType InventMathType(final Environment environment, final TyType typeA, final TyType typeB, final CanMathResult result) {
    TyType resultType = null;
    switch (result) {
      case YesAndResultIsInteger:
        resultType = new TyNativeInteger(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("int")).withPosition(typeA).withPosition(typeB);
        break;
      case YesAndResultIsLong:
        resultType = new TyNativeLong(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("long")).withPosition(typeA).withPosition(typeB);
        break;
      case YesAndResultIsDouble:
        resultType = new TyNativeDouble(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("double")).withPosition(typeA).withPosition(typeB);
        break;
      case YesAndResultIsString:
      case YesAndResultIsStringRepetitionUsingSpecialMultiplyOp:
        resultType = new TyNativeString(TypeBehavior.ReadOnlyNativeValue, null, Token.WRAP("string")).withPosition(typeA).withPosition(typeB);
        break;
    }
    return resultType;
  }

  static CanMathResult UpgradeToList(final CanMathResult result) {
    var next = result;
    switch (result) {
      case YesAndResultIsInteger:
        next = CanMathResult.YesAndResultIsListInteger;
        break;
      case YesAndResultIsDouble:
        next = CanMathResult.YesAndResultIsListDouble;
        break;
      case YesAndResultIsString:
        next = CanMathResult.YesAndResultIsListString;
        break;
      case YesAndResultIsLong:
        next = CanMathResult.YesAndResultIsListLong;
        break;
    }
    return next;
  }
}
