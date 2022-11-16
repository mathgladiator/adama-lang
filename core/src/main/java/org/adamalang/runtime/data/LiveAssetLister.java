package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

import java.util.List;

/** list assets for an object that are stored; this is for garbage collection of assets */
public interface LiveAssetLister {

  /** list all the asset ids for a given key */
  public void list(Key key, Callback<List<String>> callback);
}
