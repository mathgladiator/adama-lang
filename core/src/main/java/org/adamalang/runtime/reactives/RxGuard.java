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
import java.util.TreeMap;

/**
 * a condition to learn if changes have occured. This is like a Lazy, but gives people the ability
 * to learn if changes have happened since the last time a commited happened
 */
public class RxGuard extends RxDependent {
  private int generation;
  private TreeMap<Integer, Integer> bumps;

  public RxGuard(RxParent parent) {
    super(parent);
    generation = 0;
    __invalid = true;
    bumps = null;
  }

  @Override
  public boolean alive() {
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  private void inc() {
    if (__parent instanceof RxRecordBase && generation == 0) {
      generation = ((RxRecordBase) __parent).__id();
    }
    generation *= 65521;
    generation++;
  }

  @Override
  public boolean __raiseInvalid() {
    if (!__invalid) {
      inc();
      __invalidateSubscribers();
    }
    if (__parent != null) {
      return __parent.__isAlive();
    }
    return true;
  }

  public void __settle(Set<Integer> viewers) {
    __lowerInvalid();
  }

  public int getGeneration(int viewerId) {
    if (generation == 0) {
      inc();
    }
    int bump = 0;
    if (bumps != null) {
      Integer bumpTest = bumps.get(viewerId);
      if (bumpTest != null) {
        bump = bumpTest;
      }
    }
    if (isFired(viewerId)) {
      if (bumps == null) {
        bumps = new TreeMap<>();
      }
      Integer val = bumps.get(viewerId);
      if (val == null) {
        val = 0;
      }
      val += generation * 17;
      bumps.put(viewerId, val);
      bump = val;
    }
    return generation + bump;
  }
}
