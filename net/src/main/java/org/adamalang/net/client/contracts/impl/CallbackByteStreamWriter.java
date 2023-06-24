/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
