/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.contracts;

import org.adamalang.runtime.exceptions.AbortMessageException;

/** this is a lazy way of associating code to run within a queue. It's basically
 * a runnable that can throw an abort */
@FunctionalInterface
public interface AsyncAction {
  /** execute the given task, and maybe abort */
  public void execute() throws AbortMessageException;
}
