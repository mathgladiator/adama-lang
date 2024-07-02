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
package org.adamalang.runtime.async;

import org.adamalang.runtime.reactives.RxInt32;

import java.util.ArrayList;

/** a memoization of the produced ids. This serves to make injected table ids to be deterministic during an async flow */
public class IdHistoryLog {
  private final ArrayList<Integer> history;
  private int at;

  public IdHistoryLog() {
    this.history = new ArrayList<>();
    this.at = 0;
  }

  public void revert() {
    at = 0;
  }

  public void commit() {
    history.clear();
  }

  public int next(RxInt32 basis) {
    final int v;
    if (at < history.size()) {
      v = history.get(at);
    } else {
      v = basis.bumpUpPre();
      history.add(v);
    }
    at++;
    return v;
  }
}
