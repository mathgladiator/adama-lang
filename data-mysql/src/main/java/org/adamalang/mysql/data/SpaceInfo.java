/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

import java.util.Set;

public class SpaceInfo {
  public final int id;
  public final int owner;
  public final Set<Integer> developers;
  public final boolean enabled;
  public final long storageBytes;

  public SpaceInfo(int id, int owner, Set<Integer> developers, boolean enabled, long storageBytes) {
    this.id = id;
    this.owner = owner;
    this.developers = developers;
    this.enabled = enabled;
    this.storageBytes = storageBytes;
  }
}
