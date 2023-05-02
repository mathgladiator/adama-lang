/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.translator.env;

import java.util.HashMap;
import java.util.HashSet;

/** an environment for computing free variables */
public class FreeEnvironment {
  public final HashSet<String> free;
  private final FreeEnvironment parent;
  private final HashMap<String, String> translation;
  private final HashSet<String> defined;

  private FreeEnvironment(final FreeEnvironment parent, HashMap<String, String> translation, HashSet<String> free) {
    this.parent = parent;
    this.translation = translation;
    this.defined = new HashSet<>();
    this.free = free;
  }

  /** require a variable */
  public void require(String variable) {
    if (defined.contains(variable)) {
      return;
    }
    if (parent != null) {
      parent.require(variable);
      return;
    }
    String translated = translation.get(variable);
    if (translated != null) {
      free.add(translated);
    } else {
      free.add(variable);
    }
  }

  /** define a variable */
  public void define(String variable) {
    defined.add(variable);
  }

  /** push a new scope boundary for logic */
  public FreeEnvironment push() {
    return new FreeEnvironment(this, translation, free);
  }

  /** create a new environment */
  public static FreeEnvironment root() {
    return new FreeEnvironment(null, new HashMap<>(), new HashSet<>());
  }

  /** create a new environment */
  public static FreeEnvironment record(HashMap<String, String> recordFieldTranslation) {
    return new FreeEnvironment(null, recordFieldTranslation, new HashSet<>());
  }

  @Override
  public String toString() {
    return "free:" + String.join(", ", free);
  }
}
