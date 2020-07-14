/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.types.natives.functions;

import java.util.ArrayList;
import org.adamalang.translator.tree.types.natives.TyNativeFunctional;

// this is.. SUPER strange, and this exists be life is strange
public class TyNativeFunctionInternalFieldReplacement extends TyNativeFunctional {
  public TyNativeFunctionInternalFieldReplacement(final String name, final ArrayList<FunctionOverloadInstance> overloads, final FunctionStyleJava style) {
    super(name, overloads, style);
  }
}
