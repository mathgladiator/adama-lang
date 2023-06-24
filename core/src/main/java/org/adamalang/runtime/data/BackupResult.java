/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

/** the tuple of data which comes from backing up data */
public class BackupResult {
  public final String archiveKey;
  public final int seq;
  public final long deltaBytes;
  public final long assetBytes;

  public BackupResult(String archiveKey, int seq, long deltaBytes, long assetBytes) {
    this.archiveKey = archiveKey;
    this.seq = seq;
    this.deltaBytes = deltaBytes;
    this.assetBytes = assetBytes;
  }
}
