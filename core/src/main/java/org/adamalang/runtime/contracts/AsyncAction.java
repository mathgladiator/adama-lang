/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.exceptions.AbortMessageException;

/** this is a lazy way of associating code to run within a queue. It's basically
 * a runnable that can throw an abort */
@FunctionalInterface
public interface AsyncAction {
  /** execute the given task, and maybe abort */
  public void execute() throws AbortMessageException;
}
