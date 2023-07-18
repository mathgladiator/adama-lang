/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.caravan.index;

import org.adamalang.caravan.entries.DelKey;
import org.adamalang.caravan.entries.MapKey;
import org.adamalang.runtime.data.Key;
import org.junit.Assert;
import org.junit.Test;

public class KeyMapTests {
  @Test
  public void flow() {
    KeyMap km = new KeyMap();
    {
      Key k1 = new Key("space", "key");
      MapKey mk1 = km.inventAndApply(k1);
      Assert.assertEquals(1, mk1.id);
      Assert.assertEquals(1, (int) km.get(k1));
      Assert.assertNull(km.inventAndApply(k1));
    }
    km.apply(new DelKey(new Key("space", "del")));
    km.apply(new DelKey(new Key("space", "key")));
    km.apply(new MapKey(new Key("s", "k"), 42));
    {
      Key k1 = new Key("space", "key");
      MapKey mk1 = km.inventAndApply(k1);
      Assert.assertEquals(43, mk1.id);
      Assert.assertEquals(43, (int) km.get(k1));
    }
    Assert.assertEquals(42, (int) km.get(new Key("s", "k")));
  }
}
