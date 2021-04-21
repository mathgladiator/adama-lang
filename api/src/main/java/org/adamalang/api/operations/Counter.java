/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.api.operations;

import java.util.concurrent.atomic.AtomicInteger;

/** a single counter */
public class Counter {
  private AtomicInteger value;

  public Counter() {
    this.value = new AtomicInteger(0);
  }

  public void bump() {
    value.incrementAndGet();
  }

  public int getAndReset() {
    int val = value.get();
    value.addAndGet(-val);
    return val;
  }
}
