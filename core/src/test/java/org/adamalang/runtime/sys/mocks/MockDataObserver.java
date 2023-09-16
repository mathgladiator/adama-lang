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
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.DataObserver;

import java.util.ArrayList;

public class MockDataObserver implements DataObserver {
  public ArrayList<String> writes = new ArrayList<>();

  @Override
  public synchronized void start(String snapshot) {
    writes.add("START:" + snapshot);
  }

  @Override
  public synchronized void change(String delta) {
    writes.add("DELTA:" + delta);
  }

  @Override
  public synchronized void failure(ErrorCodeException exception) {
    writes.add("FAILURE:" + exception.code);
  }

  public void dump(String intro) {
    System.err.println("MockDataObserver:" + intro);
    int at = 0;
    for (String write : writes) {
      System.err.println(at + "|" + write);
      at++;
    }
  }
}
