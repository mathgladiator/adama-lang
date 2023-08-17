/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.data;

/** the type of a document location (on a machine or in the cloud) */
public enum LocationType {
  // a single machine
  Machine(2),

  // an archive
  Archive(4);

  public final int type;

  LocationType(int type) {
    this.type = type;
  }

  public static LocationType fromType(int type) {
    for (LocationType location : LocationType.values()) {
      if (location.type == type) {
        return location;
      }
    }
    return null;
  }
}
