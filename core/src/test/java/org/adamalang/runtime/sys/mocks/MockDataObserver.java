/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
