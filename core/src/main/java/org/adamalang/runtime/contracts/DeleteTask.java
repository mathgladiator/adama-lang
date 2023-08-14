package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

public interface DeleteTask {

  public void executeAfterMark(Callback<Void> callback);

  public static final DeleteTask TRIVIAL = new DeleteTask() {
    @Override
    public void executeAfterMark(Callback<Void> callback) {
      callback.success(null);
    }
  };

  public static Callback<Void> SKIP(DeleteTask task, Callback<Void> callback) {
    return new Callback<Void>() {
      @Override
      public void success(Void value) {
        task.executeAfterMark(callback);
      }

      @Override
      public void failure(ErrorCodeException ex) {
        callback.failure(ex);
      }
    };
  }
}
