/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
