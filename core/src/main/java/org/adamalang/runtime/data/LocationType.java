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
