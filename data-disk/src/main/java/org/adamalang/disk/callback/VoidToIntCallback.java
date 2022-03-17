/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.callback;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

public class VoidToIntCallback implements Callback<Void> {
  private final int held;
  private final Callback<Integer> callback;

  public VoidToIntCallback(int held, Callback<Integer> callback) {
    this.held = held;
    this.callback = callback;
  }

  @Override
  public void success(Void value) {
    callback.success(held);
  }

  @Override
  public void failure(ErrorCodeException ex) {
    callback.failure(ex);
  }
}
