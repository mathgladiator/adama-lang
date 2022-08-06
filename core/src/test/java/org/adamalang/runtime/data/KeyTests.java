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
