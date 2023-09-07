/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.natives.algo;

/** a field that has been parsed along with a +/- */
public class CompareField {
  public final String name;
  public final boolean desc;

  public CompareField(String name, boolean desc) {
    this.name = name;
    this.desc = desc;
  }
}
