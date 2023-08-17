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
    this.archiveKey = archiveKey;
    this.deleted = deleted;
  }
}
