package org.adamalang.disk;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.disk.wal.WriteAheadMessage;

public class ApplyMessageCallback<T> implements Callback<T> {
  private final DocumentMemoryLog memory;
  private final WriteAheadMessage message;
  private final Callback<T> callback;

  public ApplyMessageCallback(DocumentMemoryLog memory, WriteAheadMessage message, Callback<T> callback) {
    this.memory = memory;
    this.message = message;
    this.callback = callback;
  }

  @Override
  public void success(T value) {
    message.apply(memory);
    callback.success(value);
  }

  @Override
  public void failure(ErrorCodeException ex) {
    callback.failure(ex);
  }
}
