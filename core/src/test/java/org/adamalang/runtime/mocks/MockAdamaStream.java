/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.mocks;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.contracts.AdamaStream;

public class MockAdamaStream implements AdamaStream {
  private final StringBuilder writer;

  public MockAdamaStream() {
    this.writer = new StringBuilder();
  }

  @Override
  public String toString() {
    return writer.toString();
  }

  @Override
  public void update(String newViewerState) {
    writer.append("UPDATE:" + newViewerState + "\n");
  }

  @Override
  public void send(String channel, String marker, String message, Callback<Integer> callback) {
    writer.append("SEND:" + channel + "/" + marker + "/" + message + "\n");
    if (channel.equals("failure")) {
      callback.failure(new ErrorCodeException(-1));
    } else {
      callback.success(123);
    }
  }

  @Override
  public void password(String password, Callback<Integer> callback) {
    writer.append("PASSWORD!\n");
    if (password.equals("failed")) {
      callback.failure(new ErrorCodeException(-23));
    } else {
      callback.success(42);
    }
  }

  @Override
  public void canAttach(Callback<Boolean> callback) {
    writer.append("CANATTACH\n");
    callback.success(true);
    callback.failure(new ErrorCodeException(-2));
  }

  @Override
  public void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback) {
    writer.append("ATTACH:" + id + "/" + name + "/" + contentType + "/" + size + "/" + md5 + "/" + sha384 + "\n");
    if (id.equals("failure")) {
      callback.failure(new ErrorCodeException(-2));
    } else {
      callback.success(1);
    }
  }

  @Override
  public void close() {
    writer.append("CLOSE\n");
  }
}
