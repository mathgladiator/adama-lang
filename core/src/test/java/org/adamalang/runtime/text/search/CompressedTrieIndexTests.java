/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.text.search;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class CompressedTrieIndexTests {
  @Test
  public void flow() {
    CompressedTrieIndex index = new CompressedTrieIndex();
    index.map("theword", 42);
    index.map("theword", 23);
    TreeSet<Integer> keys = index.keysOf("theword");
    Assert.assertTrue(keys.contains(42));
    Assert.assertTrue(keys.contains(23));
    Assert.assertFalse(keys.contains(-1));
    index.unmap("theword", 42);
    index.unmap("theword", 23);
    Assert.assertFalse(keys.contains(42));
    Assert.assertFalse(keys.contains(23));
  }
}
