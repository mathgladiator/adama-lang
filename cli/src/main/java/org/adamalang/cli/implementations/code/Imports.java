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
package org.adamalang.cli.implementations.code;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;

/** import */
public class Imports {
  public static void fill(File imports, String prefix, HashMap<String, String> map) throws Exception {
    if (imports.exists() && imports.isDirectory()) {
      for (File f : imports.listFiles()) {
        if (f.getName().endsWith(".adama")) {
          String name = prefix + f.getName().substring(0, f.getName().length() - 6);
          map.put(name, Files.readString(f.toPath()));
        } else if (f.isDirectory()) {
          fill(f, prefix + f.getName() + "/", map);
        }
      }
    }
  }

  public static HashMap<String, String> get(String imports) throws Exception {
    HashMap<String, String> map = new HashMap<>();
    File fileImports = new File(imports);
    fill(fileImports, "", map);
    return map;
  }
}
