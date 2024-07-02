/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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

/** a formula that is cached by time (or evaluated exactly once) */
public class RxCachedLazy<Ty> extends RxDependent {
  private final Supplier<Ty> formula;
  private final Supplier<Runnable> perf;
  private final long millisecondsToKeep;
  private final RxInt64 time;
  protected Ty cached;
  private int generation;
  private long computedAt;

  public RxCachedLazy(final RxParent parent, final Supplier<Ty> formula, final Supplier<Runnable> perf, int secondsToKeep, RxInt64 time) {
    super(parent);
    this.formula = formula;
    this.perf = perf;
    this.millisecondsToKeep = secondsToKeep * 1000L;
    this.time = time;
    this.cached = null;
    this.generation = 0;
    this.computedAt = 0;
  }

  public boolean alive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
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
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  public Ty get() {
    if (cached == null) {
      cached = computeWithGuard();
      inc();
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
    Ty result = formula.get();
    computedAt = time.get();
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
    if (millisecondsToKeep > 0) {
      long since = time.get() - computedAt;
      if (since > millisecondsToKeep && cached != null) {
        inc();
        cached = null;
        __invalidateSubscribers();
        __lowerInvalid();
      }
    }
  }
}
