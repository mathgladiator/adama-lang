package org.adamalang.runtime.data.mocks;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.data.PostDocumentDelete;

import java.util.ArrayList;

public class MockPostDocumentDelete implements PostDocumentDelete {
  public final ArrayList<Key> deleted;

  public MockPostDocumentDelete() {
    this.deleted = new ArrayList<>();
  }

  @Override
  public void deleteAllAssets(Key key, Callback<Void> callback) {
    deleted.add(key);
    callback.success(null);
  }
}
