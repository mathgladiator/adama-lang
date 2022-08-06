package org.adamalang.runtime.data;

import org.junit.Assert;
import org.junit.Test;

public class KeyTests {
  @Test
  public void compare() {
    Key a = new Key("space", "a");
    Key b = new Key("space", "b");
    Key c = new Key("z", "b");
    Assert.assertEquals(0, a.compareTo(a));
    Assert.assertEquals(-1, a.compareTo(b));
    Assert.assertEquals(1, b.compareTo(a));
    Assert.assertEquals(-7, b.compareTo(c));
  }

  @Test
  public void equals() {
    Key a = new Key("space", "a");
    Key b = new Key("space", "b");
    Key c = new Key("z", "b");
    Assert.assertEquals(a, a);
    Assert.assertNotEquals(a, b);
    Assert.assertNotEquals(null, c);
    Assert.assertNotEquals("space", a);
  }

}
