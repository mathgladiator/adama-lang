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
package org.adamalang.translator.tree.common;

import java.util.LinkedHashSet;

/** bundle of all the ways to watch */
public class WatchSet {
  public final LinkedHashSet<String> variables;
  public final LinkedHashSet<String> tables;
  public final LinkedHashSet<String> maps;
  public final LinkedHashSet<String> pubsub;
  public final LinkedHashSet<String> services;

  public WatchSet() {
    this.variables = new LinkedHashSet<>();
    this.tables = new LinkedHashSet<>();
    this.maps = new LinkedHashSet<>();
    this.pubsub = new LinkedHashSet<>();
    this.services = new LinkedHashSet<>();
  }
}
