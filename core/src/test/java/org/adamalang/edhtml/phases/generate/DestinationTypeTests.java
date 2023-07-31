/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.edhtml.phases.generate;

import org.junit.Assert;
import org.junit.Test;

public class DestinationTypeTests {
  @Test
  public void trivial() {
    Assert.assertTrue(DestinationType.Set.equals(DestinationType.valueOf("Set")));
  }
}