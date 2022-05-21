package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

/** a data service which backup data */
public interface ArchivingDataService extends DataService {
  /** restore a file (must be idempotent) */
  public void restore(Key key, String archiveKey, Callback<Void> callback);

  /** backup a document, returning an archiveKey */
  public void backup(Key key, Callback<String> callback);
}
