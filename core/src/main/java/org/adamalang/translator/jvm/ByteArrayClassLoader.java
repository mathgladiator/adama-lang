/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
