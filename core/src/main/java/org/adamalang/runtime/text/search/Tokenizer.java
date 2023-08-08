/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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
