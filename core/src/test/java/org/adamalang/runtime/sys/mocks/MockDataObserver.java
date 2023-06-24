/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.mocks;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.DataObserver;

import java.util.ArrayList;

public class MockDataObserver implements DataObserver {
  public ArrayList<String> writes = new ArrayList<>();

  @Override
  public synchronized void start(String snapshot) {
    writes.add("START:" + snapshot);
  }

  @Override
  public synchronized void change(String delta) {
    writes.add("DELTA:" + delta);
  }

  @Override
  public synchronized void failure(ErrorCodeException exception) {
    writes.add("FAILURE:" + exception.code);
  }

  public void dump(String intro) {
    System.err.println("MockDataObserver:" + intro);
    int at = 0;
    for (String write : writes) {
      System.err.println(at + "|" + write);
      at++;
    }
  }
}
