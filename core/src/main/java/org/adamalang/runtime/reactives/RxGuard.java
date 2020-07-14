/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a condition to learn if changes have occured. This is like a Lazy, but gives people the ability to learn if changes have happened since the last time a commited happened */
public class RxGuard extends RxBase implements RxChild {
  boolean invalid;

  public RxGuard() {
    super(null);
    invalid = true;
  }

  @Override
  public void __commit(final String name, final ObjectNode delta) {
    invalid = false;
  }

  @Override
  public boolean __raiseInvalid() {
    invalid = true;
    return true;
  }

  @Override
  public void __revert() {
  }
}
