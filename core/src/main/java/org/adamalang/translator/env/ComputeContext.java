/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.translator.env;

/** code runs within a compute context. Either the code is on the left side and
 * assignable, or on the right and is a native compute value */
public enum ComputeContext {
  Assignment, // lvalue
  Computation, // rvalue
  Unknown
}
