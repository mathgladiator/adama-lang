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
package org.adamalang.support.testgen;

import java.util.regex.Pattern;

public class TestFile {
  public final String clazz;
  public final String name;
  public final boolean success;

  public TestFile(final String clazz, final String name, final boolean success) {
    this.clazz = clazz;
    this.name = name;
    this.success = success;
    if (name.contains("_") || clazz.contains("_")) {
      throw new RuntimeException("name and class can not contain underscore(_)");
    }
  }

  public static TestFile fromFilename(final String filename) {
    final var parts = filename.split(Pattern.quote("_"));
    return new TestFile(parts[0], parts[1], "success.a".equals(parts[2]));
  }

  public String filename() {
    return clazz + "_" + name + "_" + (success ? "success" : "failure") + ".a";
  }
}
