/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
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
