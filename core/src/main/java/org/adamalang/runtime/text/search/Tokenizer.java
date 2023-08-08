package org.adamalang.runtime.text.search;

import java.util.PrimitiveIterator;
import java.util.TreeSet;

/** a very simple tokenizer */
public class Tokenizer {
  public static TreeSet<String> of(String sentence) {
    TreeSet<String> result = new TreeSet<>();
    StringBuilder word = new StringBuilder();
    PrimitiveIterator.OfInt it = sentence.codePoints().iterator();
    while (it.hasNext()) {
      int cp = it.nextInt();
      if (Character.isAlphabetic(cp)) {
        word.append(Character.toString(Character.toLowerCase(cp)));
      } else {
        if (word.length() > 0 && Character.isWhitespace(cp)) {
          result.add(word.toString());
          word.setLength(0);
        }
      }
    }
    if (word.length() > 0) {
      result.add(word.toString());
    }
    return result;
  }
}
