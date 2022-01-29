/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.mysql.backend.data;

public class DocumentIndex {
  public final String key;
  public final String created;
  public final String updated;
  public final int seq;
  public final boolean active;

  public DocumentIndex(String key, String created, String updated, int seq, boolean active) {
    this.key = key;
    this.created = created;
    this.updated = updated;
    this.seq = seq;
    this.active = active;
  }
}
