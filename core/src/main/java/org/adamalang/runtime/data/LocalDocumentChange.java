/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.data;

/** the local copy of the document should be changed by incorporating the given patch */
public class LocalDocumentChange {
  public final String patch;
  public final int reads;
  public final int seq;

  public LocalDocumentChange(String patch, int reads, int seq) {
    this.patch = patch;
    this.reads = reads;
    this.seq = seq;
  }
}
