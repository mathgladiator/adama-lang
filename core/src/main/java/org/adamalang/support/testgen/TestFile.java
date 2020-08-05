/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.support.testgen;

import java.util.regex.Pattern;

public class TestFile {
  public static TestFile fromFilename(final String filename) {
    final var parts = filename.split(Pattern.quote("_"));
    return new TestFile(parts[0], parts[1], "success.a".equals(parts[2]));
  }

  public final String clazz;
  public final String name;
  public final boolean success;

  public TestFile(final String clazz, final String name, final boolean success) {
    this.clazz = clazz;
    this.name = name;
    this.success = success;
    if (name.contains("_") || clazz.contains("_")) { throw new RuntimeException("name and class can not contain understore(_)"); }
  }

  public String filename() {
    return clazz + "_" + name + "_" + (success ? "success" : "failure") + ".a";
  }
}
