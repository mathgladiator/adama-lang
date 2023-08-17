/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

import org.junit.Assert;
import org.junit.Test;

public class FinderServiceTests {

  @Test
  public void location_tests() {
    Assert.assertEquals(2, LocationType.Machine.type);
    Assert.assertEquals(4, LocationType.Archive.type);
    Assert.assertEquals(LocationType.Machine, LocationType.fromType(2));
    Assert.assertEquals(LocationType.Archive, LocationType.fromType(4));
    Assert.assertNull(LocationType.fromType(0));
  }

  @Test
  public void result_coverage() {
    new DocumentLocation(1L, LocationType.Machine, "region", "value", "archive", false);
  }
}
