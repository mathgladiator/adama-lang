/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.translator.tree.privacy;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.junit.Test;

public class PrivacyPolicyTests {
  @Test
  public void coverage() {
    new PrivatePolicy(null).writePrivacyCheckGuard(null, null, null);
    new PrivatePolicy(null).writeTypeReflectionJson(new JsonStreamWriter());
  }
}
