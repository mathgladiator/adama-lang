package org.adamalang.runtime.text.search;

import org.junit.Assert;
import org.junit.Test;

public class StopWordsTest {
  @Test
  public void flow() {
    Assert.assertTrue(StopWords.LIST.contains("the"));
    Assert.assertTrue(StopWords.LIST.contains("a"));
    Assert.assertTrue(StopWords.LIST.contains("of"));
  }
}
