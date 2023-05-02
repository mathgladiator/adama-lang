/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
