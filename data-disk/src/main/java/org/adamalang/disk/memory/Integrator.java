/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.disk.memory;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.wal.WriteAheadMessage;

public class Integrator {
  public <T> Callback<T> write(WriteAheadMessage message, Callback<T> user) {
    return new Callback<T>() {
      @Override
      public void success(T value) {
        // the item was written to disk
        // NOW, integrate into the local copy
        // then emit a user success
      }

      @Override
      public void failure(ErrorCodeException ex) {
        user.failure(ex);
      }
    };
  }
}
