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
package org.adamalang.runtime.deploy;

import org.adamalang.runtime.json.JsonStreamReader;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

/** a version of an Adama script with imports included */
public class DeployedVersion {
  public final String main;
  public final TreeMap<String, String> includes;

  public DeployedVersion(JsonStreamReader reader) {
    String _main = "";
    includes = new TreeMap<>();
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "main":
            _main = reader.readString();
            break;
          case "includes":
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                String name = reader.fieldName();
                includes.put(name, reader.readString());
              }
            } else {
              reader.skipValue();
            }
            break;
          default:
            reader.skipValue();
        }
      }
    } else {
      _main = reader.readString();
    }
    this.main = _main;
  }

  @Override
  public int hashCode() {
    return Objects.hash(main, includes);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeployedVersion that = (DeployedVersion) o;
    return Objects.equals(main, that.main) && Objects.equals(includes, that.includes);
  }
}
