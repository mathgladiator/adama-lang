package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

public interface ArchivingDataService extends DataService {
  public void restore(Key key, Callback<Void> callback);

  public void backup(Key key, Callback<Void> callback);
}
