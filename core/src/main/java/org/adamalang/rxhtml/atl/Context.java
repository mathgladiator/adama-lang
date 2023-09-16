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
package org.adamalang.rxhtml.atl;

import java.util.HashMap;

public class Context {
  public static final Context DEFAULT = new Context(false);
  public final boolean is_class;
  public final HashMap<String, Integer> freq;

  private Context(boolean is_class) {
    this.is_class = is_class;
    if (is_class) {
      freq = new HashMap<>();
    } else {
      freq = null;
    }
  }

  public static final Context makeClassContext() {
    return new Context(true);
  }

  public void cssTrack(String fragment) {
    Integer prior = freq.get(fragment);
    if (prior == null) {
      freq.put(fragment, 1);
    } else {
      freq.put(fragment, prior + 1);
    }
  }
}
