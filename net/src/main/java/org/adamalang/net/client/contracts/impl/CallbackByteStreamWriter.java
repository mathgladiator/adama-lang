/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.contracts.impl;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;

public abstract class CallbackByteStreamWriter<T> implements Callback<ByteStream> {
  private final Callback<T> callback;

  public CallbackByteStreamWriter(Callback<T> callback) {
    this.callback = callback;
  }

  @Override
  public void success(ByteStream value) {
    write(value);
  }

  public abstract void write(ByteStream stream);

  @Override
  public void failure(ErrorCodeException ex) {
    callback.failure(ex);
  }
}
