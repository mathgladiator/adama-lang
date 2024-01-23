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

import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.Set;
import java.util.function.Supplier;

/** a reactive lazy formula which is computed on demand */
public class RxLazy<Ty> extends RxDependent {
  private final Supplier<Ty> formula;
  private final Supplier<Runnable> perf;
  protected Ty cached;
  private int generation;

  public RxLazy(final RxParent parent, final Supplier<Ty> formula, final Supplier<Runnable> perf) {
    super(parent);
    this.formula = formula;
    this.cached = null;
    this.generation = 0;
    this.perf = perf;
  }

  public boolean alive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  @Override
  public boolean __raiseInvalid() {
    cached = null;
    __invalidateSubscribers();
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  public Ty get() {
    if (__invalid) {
      Runnable track = null;
      if (perf != null) {
        track = perf.get();
      }
      Ty result = formula.get();
      if (track != null) {
        track.run();
      }
      return result;
    }
    if (cached == null) {
      cached = computeWithGuard();
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

  private Ty computeWithGuard() {
    Runnable track = null;
    if (perf != null) {
      track = perf.get();
    }
    start();
    Ty result = formula.get();
    finish();
    if (track != null) {
      track.run();
    }
    return result;
  }

  public int getGeneration() {
    if (generation == 0) {
      inc();
    }
    return generation;
  }

  public void __settle(Set<Integer> views) {
    if (__invalid) {
      cached = null;
      inc();
      __lowerInvalid();
    }
  }

  public void __forceSettle() {
    if (__invalid) {
      inc();
      __lowerInvalid();
      get();
    }
  }
}
