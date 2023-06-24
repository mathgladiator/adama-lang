/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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
