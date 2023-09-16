/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.reactives;

import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.function.Supplier;

/** a reactive lazy formula which is computed on demand */
public class RxLazy<Ty> extends RxBase implements RxChild {
  private final Supplier<Ty> formula;
  protected Ty cached;
  private int generation;
  private boolean invalid;

  public RxLazy(final RxParent parent, final Supplier<Ty> formula) {
    super(parent);
    this.formula = formula;
    this.cached = null;
    this.invalid = false;
    this.generation = 0;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
  }

  @Override
  public void __dump(final JsonStreamWriter writer) {
  }

  @Override
  public void __insert(final JsonStreamReader reader) {
    reader.skipValue();
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    reader.skipValue();
  }

  @Override
  public void __revert() {
  }

  @Override
  public boolean __raiseInvalid() {
    if (invalid) {
      if (__parent != null) {
        return __parent.__isAlive();
      }
      return true;
    }
    invalid = true;
    cached = null;
    __invalidateSubscribers();
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  public Ty get() {
    if (invalid) {
      return formula.get();
    }
    if (cached == null) {
      cached = formula.get();
    }
    return cached;
  }

  private void inc() {
    if (__parent instanceof RxRecordBase && generation == 0) {
      generation = ((RxRecordBase) __parent).__id();
    } else if (generation == 0 && cached != null) {
      generation = cached.hashCode();
    }
    generation *= 65521;
    generation++;
  }

  /** this has the impact of ensuring the value is cached after being invalidated */
  public void dropInvalid() {
    if (checkInvalidAndLower()) {
      cached = null;
      inc();
    }
  }

  private void ensureCacheValid() {
    if (checkInvalidAndLower() || cached == null) {
      cached = formula.get();
      inc();
    }
  }

  protected boolean checkInvalidAndLower() {
    if (invalid) {
      invalid = false;
      return true;
    }
    return false;
  }

  public int getGeneration() {
    if (generation == 0) {
      inc();
    }
    ensureCacheValid();
    return generation;
  }
}
