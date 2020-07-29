/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.statements;

/** does the code block return or not */
public enum ControlFlow {
  Open, // if this is a function, then it does not return a value
  Returns // any statements after this imply dead code
}
