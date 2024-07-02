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

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/** responsible for capturing results from the java compiler */
@SuppressWarnings("unchecked")
public class ByteArrayJavaFileManager extends ForwardingJavaFileManager {
  private Map<String, byte[]> classes;

  public ByteArrayJavaFileManager(final JavaFileManager fileManager) {
    super(fileManager);
    classes = new TreeMap<>();
  }

  public static ArrayList<JavaFileObject> turnIntoCompUnits(final String fileName, final String code) {
    final var compUnits = new ArrayList<JavaFileObject>();
    compUnits.add(new SingleSourceJavaObject(fileName, code));
    return compUnits;
  }

  public Map<String, byte[]> getClasses() {
    return classes;
  }

  @Override
  public JavaFileObject getJavaFileForOutput(final JavaFileManager.Location location, final String className, final Kind kind, final FileObject sibling) throws IOException {
    return new ClassByteArrayOutputBuffer(className);
  }

  @Override
  public void flush() throws IOException {
  }

  @Override
  public void close() throws IOException {
    classes = null;
  }

  private static class SingleSourceJavaObject extends SimpleJavaFileObject {
    final String source;

    SingleSourceJavaObject(final String fileName, final String code) {
      super(URI.create(new StringBuilder().append("code:///").append(fileName).toString()), Kind.SOURCE);
      source = code;
    }

    @Override
    public CharBuffer getCharContent(final boolean ignoreEncodingErrors) {
      return CharBuffer.wrap(source);
    }
  }

  private class ClassByteArrayOutputBuffer extends SimpleJavaFileObject {
    private final String name;

    ClassByteArrayOutputBuffer(final String name) {
      super(URI.create(new StringBuilder().append("code:///").append(name).toString()), Kind.CLASS);
      this.name = name;
    }

    @Override
    public OutputStream openOutputStream() {
      return new FilterOutputStream(new ByteArrayOutputStream()) {
        @Override
        public void close() throws IOException {
          out.close();
          final var bos = (ByteArrayOutputStream) out;
          classes.put(name, bos.toByteArray());
        }
      };
    }
  }
}
