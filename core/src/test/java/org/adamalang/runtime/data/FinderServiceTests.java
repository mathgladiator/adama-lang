/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

import org.junit.Assert;
import org.junit.Test;

public class FinderServiceTests {

  @Test
  public void location_tests() {
    Assert.assertEquals(2, FinderService.Location.Machine.type);
    Assert.assertEquals(4, FinderService.Location.Archive.type);
    Assert.assertEquals(FinderService.Location.Machine, FinderService.Location.fromType(2));
    Assert.assertEquals(FinderService.Location.Archive, FinderService.Location.fromType(4));
    Assert.assertNull(FinderService.Location.fromType(0));
  }

  @Test
  public void result_coverage() {
    new FinderService.Result(1L, FinderService.Location.Machine, "region", "value", "archive");
  }
}
