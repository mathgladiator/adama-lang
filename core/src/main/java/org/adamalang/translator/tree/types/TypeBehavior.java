/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types;

public enum TypeBehavior {
  ReadOnlyGetNativeValue, // the type is native, and can be used natively
  ReadOnlyNativeValue, //
  ReadWriteNative, // the value is native, and can only be read from
  ReadWriteWithSetGet, // the value is native, and can only be read from
}
