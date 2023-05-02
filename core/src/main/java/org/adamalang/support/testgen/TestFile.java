/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
