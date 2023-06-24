/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.frontend;

import org.junit.Assert;
import org.junit.Test;

public class SpaceTemplatesTests {
  @Test
  public void found_chat() {
    SpaceTemplates.SpaceTemplate st = SpaceTemplates.REGISTRY.of("chat");
    Assert.assertNotNull(st);
    Assert.assertFalse(st.idearg("space").contains("TEMPLATE"));
  }
}
