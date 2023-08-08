package org.adamalang.runtime.text.search;

import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

public class TokenizerTests {
  @Test
  public void simple() {
    TreeSet<String> words = Tokenizer.of("the quick brown fox did a little dance in the woods");
    Assert.assertTrue(words.contains("the"));
    Assert.assertTrue(words.contains("quick"));
    Assert.assertTrue(words.contains("brown"));
    Assert.assertTrue(words.contains("fox"));
    Assert.assertTrue(words.contains("did"));
    Assert.assertTrue(words.contains("a"));
    Assert.assertTrue(words.contains("little"));
    Assert.assertTrue(words.contains("dance"));
    Assert.assertTrue(words.contains("woods"));
  }
}
