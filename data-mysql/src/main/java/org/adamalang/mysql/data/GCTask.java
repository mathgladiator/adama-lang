/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

/** a task for asset GC algorithm to work on */
public class GCTask {
  public final int id;
  public final String space;
  public final String key;
  public final int seq;
  public final String archiveKey;

  public GCTask(int id, String space, String key, int seq, String archiveKey) {
    this.id = id;
    this.space = space;
    this.key = key;
    this.seq = seq;
    this.archiveKey = archiveKey;
  }

  @Override
  public String toString() {
    return "GCTask{" + "id=" + id + ", space='" + space + '\'' + ", key='" + key + '\'' + ", seq=" + seq + '}';
  }
}
