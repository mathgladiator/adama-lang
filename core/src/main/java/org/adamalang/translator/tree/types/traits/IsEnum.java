/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.traits;

import org.adamalang.translator.tree.types.shared.EnumStorage;

public interface IsEnum {
  String name();
  EnumStorage storage();
}
