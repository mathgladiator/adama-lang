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
