/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

/** a task for asset GC algorithm to work on */
public class GCTask {
  public final int id;
  public final String space;
  public final String key;
  public final int seq;

  public GCTask(int id, String space, String key, int seq) {
    this.id = id;
    this.space = space;
    this.key = key;
    this.seq = seq;
  }

  @Override
  public String toString() {
    return "GCTask{" + "id=" + id + ", space='" + space + '\'' + ", key='" + key + '\'' + ", seq=" + seq + '}';
  }
}
