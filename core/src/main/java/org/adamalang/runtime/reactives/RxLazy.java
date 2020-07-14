/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import java.util.function.Supplier;
import org.adamalang.runtime.bridges.NativeBridge;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a reactive lazy formula which is computed on demand */
public class RxLazy<Ty> extends RxBase implements RxChild {
  private Ty cached;
  private final Supplier<Ty> formula;
  private boolean invalid;
  private final NativeBridge<Ty> support;

  public RxLazy(final RxParent parent, final NativeBridge<Ty> support, final Supplier<Ty> formula) {
    super(parent);
    this.support = support;
    this.formula = formula;
    this.cached = null;
    this.invalid = true;
  }

  @Override
  public void __commit(final String name, final ObjectNode delta) {
  }

  @Override
  public boolean __raiseInvalid() {
    invalid = true;
    __invalidateSubscribers();
    return true;
  }

  @Override
  public void __revert() {
  }

  protected boolean checkInvalidAndLower() {
    if (invalid) {
      invalid = false;
      return true;
    }
    return false;
  }

  public Ty get() {
    if (checkInvalidAndLower() || cached == null) {
      cached = formula.get();
    }
    return cached;
  }
}
