/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.reactives;

import java.util.function.Supplier;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** a reactive lazy formula which is computed on demand */
public class RxLazy<Ty> extends RxBase implements RxChild {
  private Ty cached;
  private final Supplier<Ty> formula;
  private int generation;
  private boolean invalid;

  public RxLazy(final RxParent parent, final Supplier<Ty> formula) {
    super(parent);
    this.formula = formula;
    this.cached = null;
    this.invalid = true;
    this.generation = 1;
  }

  @Override
  public void __commit(final String name, final JsonStreamWriter writer) {
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    reader.skipValue();
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

  private void ensureCacheValid() {
    if (checkInvalidAndLower() || cached == null) {
      cached = formula.get();
      generation++;
    }
  }

  public Ty get() {
    ensureCacheValid();
    return cached;
  }

  public int getGeneration() {
    ensureCacheValid();
    return generation;
  }
}
