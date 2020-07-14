/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.checking.properties;

public enum CanMathResult {
  No, //
  YesAndResultIsDouble, //
  YesAndResultIsInteger, //
  YesAndResultIsListDouble, //
  YesAndResultIsListInteger, //
  YesAndResultIsListLong, //
  YesAndResultIsListString, //
  YesAndResultIsLong, //
  YesAndResultIsString, //
  YesAndResultIsStringRepetitionUsingSpecialMultiplyOp //
}
