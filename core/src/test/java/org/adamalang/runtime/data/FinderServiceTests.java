package org.adamalang.runtime.data;

import org.junit.Assert;
import org.junit.Test;

public class FinderServiceTests {

  @Test
  public void location_tests() {
    Assert.assertEquals(1, FinderService.Location.Fresh.type);
    Assert.assertEquals(2, FinderService.Location.Machine.type);
    Assert.assertEquals(4, FinderService.Location.Archive.type);
    Assert.assertEquals(FinderService.Location.Fresh, FinderService.Location.fromType(1));
    Assert.assertEquals(FinderService.Location.Machine, FinderService.Location.fromType(2));
    Assert.assertEquals(FinderService.Location.Archive, FinderService.Location.fromType(4));
    Assert.assertNull(FinderService.Location.fromType(0));
  }

  @Test
  public void result_coverage() {
    new FinderService.Result(1L, FinderService.Location.Fresh, "value");
  }
}
