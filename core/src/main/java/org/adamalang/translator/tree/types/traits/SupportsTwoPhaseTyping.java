/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.types.traits;

import org.adamalang.translator.env.Environment;
import org.adamalang.translator.tree.types.TyType;

/** this contract enables expressions to be typed */
public interface SupportsTwoPhaseTyping {
  TyType estimateType(Environment environment);

  void upgradeType(Environment environment, TyType newType);
}
