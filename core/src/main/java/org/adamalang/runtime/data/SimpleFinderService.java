package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

public interface SimpleFinderService {

  /** find the location of a key */
  void find(Key key, Callback<DocumentLocation> callback);

}
