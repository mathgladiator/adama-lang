/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.env;

/** code runs within a compute context. Either the code is on the left side and
 * assignable, or on the right and is a native compute value */
public enum ComputeContext {
  Assignment, // lvalue
  Computation, // rvalue
  Unknown
}
