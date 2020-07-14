/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.expressions;

/** indicates how message conversion works, a cache-like computation */
public enum MessageConversionStyle {
  Maybe, //
  Multiple, //
  None, //
  Single
}
