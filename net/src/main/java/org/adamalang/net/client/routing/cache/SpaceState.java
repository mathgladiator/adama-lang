/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.routing.cache;

import org.adamalang.common.Hashing;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.TreeSet;

/** the collection of targets for a single space; core element of the routing table */
public class SpaceState {
  private final TreeSet<String> targets;

  public SpaceState() {
    this.targets = new TreeSet<>();
  }

  public boolean subtract(String target) {
    targets.remove(target);
    return targets.size() == 0;
  }

  public void add(String target) {
    targets.add(target);
  }

  public TreeSet<String> list() {
    return new TreeSet<>(targets);
  }

  public String pick(String key) {
    String winner = null;
    String winningHash = "";
    byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
    for (String target : targets) {
      MessageDigest digest = Hashing.md5();
      digest.update(target.getBytes(StandardCharsets.UTF_8));
      digest.update(keyBytes);
      String targetHash = Hashing.finishAndEncode(digest);
      if (targetHash.compareTo(winningHash) > 0) {
        winner = target;
        winningHash = targetHash;
      }
    }
    return winner;
  }
}
