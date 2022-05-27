/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.routing.finder;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;

import java.util.HashMap;

public class MockMachinePicker implements MachinePicker {
  private HashMap<Key, String> map;

  public MockMachinePicker() {
    this.map = new HashMap<>();
  }

  @Override
  public synchronized void pickHost(Key key, Callback<String> callback) {
    String value = map.get(key);
    if (value != null) {
      callback.success(value);
    } else {
      callback.failure(new ErrorCodeException(-123));
    }
  }
}
