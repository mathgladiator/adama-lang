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
package org.adamalang.runtime.graph;

import org.adamalang.runtime.reactives.RxRecordBase;

import java.util.function.Supplier;

public class Graph {

  public void watch(short under, RxRecordBase<?> record, short assoc, Supplier<int[]> supplier) {

  }

  public class Query {
    // expess a way to build an a query that collects a bunch of ids
  }

  public Query start() {
    return new Query();
  }
}