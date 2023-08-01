/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.rxhtml.atl;

import java.util.HashMap;

public class Context {
  public static final Context DEFAULT = new Context(false);
  public final boolean is_class;
  public final HashMap<String, Integer> freq;

  private Context(boolean is_class) {
    this.is_class = is_class;
    if (is_class) {
      freq = new HashMap<>();
    } else {
      freq = null;
    }
  }

  public static final Context makeClassContext() {
    return new Context(true);
  }

  public void cssTrack(String fragment) {
    Integer prior = freq.get(fragment);
    if (prior == null) {
      freq.put(fragment, 1);
    } else {
      freq.put(fragment, prior + 1);
    }
  }
}
