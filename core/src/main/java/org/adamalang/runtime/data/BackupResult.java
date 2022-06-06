/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.data;

/** the tuple of data which comes from backing up data */
public class BackupResult {
  public final String archiveKey;
  public final long deltaBytes;
  public final long assetBytes;

  public BackupResult(String archiveKey, long deltaBytes, long assetBytes) {
    this.archiveKey = archiveKey;
    this.deltaBytes = deltaBytes;
    this.assetBytes = assetBytes;
  }
}
