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
package org.adamalang.translator.jvm;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;

/** responsible for converting a bunch of compiled bytecode into classes discoverable by the JVM */
public class ByteArrayClassLoader extends URLClassLoader {
  private final Map<String, byte[]> classes;

  public ByteArrayClassLoader(final Map<String, byte[]> classes) {
    super(new URL[]{}, ClassLoader.getSystemClassLoader());
    this.classes = classes;
  }

  @Override
  protected Class findClass(final String className) throws ClassNotFoundException {
    final var bytes = classes.get(className);
    if (bytes != null) {
      classes.remove(className); // clean up
      return defineClass(className, bytes, 0, bytes.length);
    } else {
      throw new ClassNotFoundException(className);
    }
  }
}
