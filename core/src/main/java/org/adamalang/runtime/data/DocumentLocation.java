/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

/** where a document is located */
public class DocumentLocation {
  public final long id;
  public final LocationType location;
  public final String region;
  public final String machine;
  public final String archiveKey;
  public final boolean deleted;

  public DocumentLocation(long id, LocationType location, String region, String machine, String archiveKey, boolean deleted) {
    this.id = id;
    this.location = location;
    this.region = region;
    this.machine = machine;
    this.archiveKey = "".equals(archiveKey) ? null : archiveKey;
    this.deleted = deleted;
  }
}
