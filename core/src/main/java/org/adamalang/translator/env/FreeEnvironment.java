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
package org.adamalang.translator.env;

import java.util.HashMap;
import java.util.HashSet;

/** an environment for computing free variables */
public class FreeEnvironment {
  public final HashSet<String> free;
  private final FreeEnvironment parent;
  private final HashMap<String, String> translation;
  private final HashSet<String> defined;

  private FreeEnvironment(final FreeEnvironment parent, HashMap<String, String> translation, HashSet<String> free) {
    this.parent = parent;
    this.translation = translation;
    this.defined = new HashSet<>();
    this.free = free;
  }

  /** require a variable */
  public void require(String variable) {
    if (defined.contains(variable)) {
      return;
    }
    if (parent != null) {
      parent.require(variable);
      return;
    }
    String translated = translation.get(variable);
    if (translated != null) {
      free.add(translated);
    } else {
      free.add(variable);
    }
  }

  /** define a variable */
  public void define(String variable) {
    defined.add(variable);
  }

  /** push a new scope boundary for logic */
  public FreeEnvironment push() {
    return new FreeEnvironment(this, translation, free);
  }

  /** create a new environment */
  public static FreeEnvironment root() {
    return new FreeEnvironment(null, new HashMap<>(), new HashSet<>());
  }

  /** create a new environment */
  public static FreeEnvironment record(HashMap<String, String> recordFieldTranslation) {
    return new FreeEnvironment(null, recordFieldTranslation, new HashSet<>());
  }

  @Override
  public String toString() {
    return "free:" + String.join(", ", free);
  }
}
