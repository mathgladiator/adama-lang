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
    if (root.keys == null) {
      root.keys = new TreeSet<>();
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
        // TODO: need to clean up the children
      }
    }
  }

  public TreeSet<Integer> keysOf(String word) {
    CompressedTrieIndex index = of(word);
    return of(word).keys;
  }
}
