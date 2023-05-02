/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.mysql.data;

public class DocumentIndex {
  public final String key;
  public final String created;
  public final String updated;
  public final int seq;

  public DocumentIndex(String key, String created, String updated, int seq) {
    this.key = key;
    this.created = created;
    this.updated = updated;
    this.seq = seq;
  }
}
