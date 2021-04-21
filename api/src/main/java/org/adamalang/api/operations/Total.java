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

public class Total {
  private AtomicInteger value;

  public Total() {
    this.value = new AtomicInteger(0);
  }
  public void dec() {
    value.decrementAndGet();
  }

  public void inc() {
    value.incrementAndGet();
  }

  public void set(int val) {
    value.set(val);
  }

  public int get() {
    return value.get();
  }
}
