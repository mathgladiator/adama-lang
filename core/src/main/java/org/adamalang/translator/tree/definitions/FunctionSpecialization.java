/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.definitions;

/**
 * denote a specialization of a function. A pure function is one that requires no state from the
 * document. In the truest sense, a pure function is an actual mathematical function. These are
 * important because these can be put into a variety of unique contexts and easily translate to
 * things like JavaScript!
 *
 * <p>An impure function is really a procedure, and it is given free reign to do whatever it wants.
 */
public enum FunctionSpecialization {
  Impure, //
  Pure
}
