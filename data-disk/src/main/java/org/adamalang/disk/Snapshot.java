/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Snapshot {
  public final int seq;
  public final int history;
  public final String body;
  public final ArrayList<String> undos;

  public Snapshot(int seq, int history, String body, ArrayList<String> undos) {
    this.seq = seq;
    this.history = history;
    this.body = body;
    this.undos = undos;
  }

  public void save(File file) throws IOException {

  }

  public static Snapshot load(File file) throws IOException {
    return null;
  }
}
