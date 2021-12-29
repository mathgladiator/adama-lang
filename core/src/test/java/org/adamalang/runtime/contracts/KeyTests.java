/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.contracts;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class KeyTests {
  @Test
  public void coverageHash() {
    Key key = new Key("space", "key");
    Assert.assertTrue(key.hashCode() >= 0);
    Random rng = new Random();
    for (int k = 0; k < 1000; k++) {
      Key t = new Key("space" + System.nanoTime(), "key" + rng.nextDouble() + "/" + rng.nextLong());
    }
  }

  @Test
  public void coverageEquals() {
    Key key = new Key("space", "key");
    Assert.assertTrue(key.equals(new Key("space", "key")));
    Assert.assertTrue(key.equals(key));
    Assert.assertFalse(key.equals(null));
    Assert.assertFalse(key.equals("nope"));
    Assert.assertFalse(key.equals(new Key("spacex", "keyx")));
    Assert.assertFalse(key.equals(new Key("spacex", "key")));
    Assert.assertFalse(key.equals(new Key("space", "keyx")));
  }

  @Test
  public void coverageCompare() {
    Key key = new Key("space", "key");
    Assert.assertEquals(0, key.compareTo(key));
    Assert.assertEquals(0, key.compareTo(new Key("space", "key")));
    Assert.assertEquals(-1, key.compareTo(new Key("space1", "key")));
    Assert.assertEquals(-1, key.compareTo(new Key("space", "key1")));
    Assert.assertEquals(-1, key.compareTo(new Key("space1", "key1")));
    Assert.assertEquals(18, key.compareTo(new Key("a", "key")));
    Assert.assertEquals(18, key.compareTo(new Key("a", "key1")));
    Assert.assertEquals(18, key.compareTo(new Key("a", "key1")));
  }
}
