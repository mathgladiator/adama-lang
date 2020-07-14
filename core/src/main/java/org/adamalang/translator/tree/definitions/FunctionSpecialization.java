/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

/** denote a specialization of a function. A pure function is one that requires
 * no state from the document. In the truest sense, a pure function is an actual
 * mathematical function. These are important because these can be put into a
 * variety of unique contexts and easily translate to things like JavaScript!
 *
 * An impure function is really a procedure, and it is given free reign to do
 * whatever it wants. */
public enum FunctionSpecialization {
  Impure, //
  Pure
}
