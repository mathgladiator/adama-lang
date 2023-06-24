/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
