/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.ruleset;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.parser.token.Token;
import org.adamalang.translator.tree.types.TyType;
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
        resultType = new TyNativeInteger(Token.WRAP("int")).withPosition(typeA).withPosition(typeB);
        break;
      case YesAndResultIsLong:
        resultType = new TyNativeLong(Token.WRAP("long")).withPosition(typeA).withPosition(typeB);
        break;
      case YesAndResultIsDouble:
        resultType = new TyNativeDouble(Token.WRAP("double")).withPosition(typeA).withPosition(typeB);
        break;
      case YesAndResultIsString:
      case YesAndResultIsStringRepetitionUsingSpecialMultiplyOp:
        resultType = new TyNativeString(Token.WRAP("string")).withPosition(typeA).withPosition(typeB);
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
