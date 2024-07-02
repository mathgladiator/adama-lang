/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.text.search;

import java.util.PrimitiveIterator;
import java.util.TreeMap;
import java.util.TreeSet;

/** this is a simple trie for mapping code points that are compressed via a hash into keys */
public class CompressedTrieIndex {
  private final TreeMap<Integer, CompressedTrieIndex> children;
  private TreeSet<Integer> keys;

  public CompressedTrieIndex() {
    this.children = new TreeMap<>();
    this.keys = null;
  }

  private CompressedTrieIndex next(int k) {
    CompressedTrieIndex val = children.get(k);
    if (val == null) {
      val = new CompressedTrieIndex();
      children.put(k, val);
    }
    return val;
  }

  private CompressedTrieIndex of(String word) {
    CompressedTrieIndex root = this;
    PrimitiveIterator.OfInt it = word.codePoints().iterator();
    while (it.hasNext()) {
      int val = it.nextInt();
      if (it.hasNext()) {
        val += 32831 * it.nextInt();
      }
      if (it.hasNext()) {
        val += 52347 * it.nextInt();
      }
      root = next(val);
    }
    return root;
  }

  public void map(String word, int key) {
    CompressedTrieIndex index = of(word);
    if (index.keys == null) {
      index.keys = new TreeSet<>();
    }
    index.keys.add(key);
  }

  public void unmap(String word, int key) {
    CompressedTrieIndex index = of(word);
    if (index.keys != null) {
      index.keys.remove(key);
      if (index.keys.size() == 0) {
        index.keys = null;
        // TODO: need to clean up the parents
      }
    }
  }

  public TreeSet<Integer> keysOf(String word) {
    CompressedTrieIndex index = of(word);
    return of(word).keys;
  }
}
