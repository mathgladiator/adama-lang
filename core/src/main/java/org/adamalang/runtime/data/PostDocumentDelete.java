package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

/** simple contract to clean up artifacts on a document delete */
public interface PostDocumentDelete {
  /** delete all the assets for a key */
  public void deleteAllAssets(Key key, Callback<Void> callback);
}
