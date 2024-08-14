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
package org.adamalang.rxhtml.routing;

import org.adamalang.common.cache.Measurable;

import java.util.TreeMap;
import java.util.function.Function;

/** a simple table for routing a URI to a Target */
public class Table implements Measurable {
  private final Path root;
  private long memoryCached;

  public Table() {
    this.root = new Path(null);
    memoryCached = this.root.memory();
  }

  public boolean add(Instructions instructions, Target target) {
    Path at = root;
    for (Function<Path, Path> delta : instructions.progress) {
      at = delta.apply(at);
    }
    boolean result = at.set(target);
    memoryCached = this.root.memory();
    return result;
  }

  public Target route(String path, TreeMap<String, String> captures) {
    return root.route(0, Path.parsePath(path), captures);
  }

  @Override
  public long measure() {
    return memoryCached;
  }
}
