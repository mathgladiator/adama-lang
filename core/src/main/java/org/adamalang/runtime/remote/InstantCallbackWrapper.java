package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** experimental callback wrapper to instantly detect a return value */
public class InstantCallbackWrapper<T> implements Callback<T> {
  private final Callback<T> original;
  private T value;
  private Consumer<T> action;

  public InstantCallbackWrapper(Callback<T> original) {
    this.original = original;
  }

  @Override
  public void success(T value) {
    set(value).get();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    original.failure(ex);
  }

  public synchronized Supplier<Boolean> set(T value) {
    this.value = value;
    if (this.action != null) {
      return () -> { action.accept(value); return true; };
    } else {
      return () -> { return false; };
    }
  }

  public synchronized Supplier<Boolean> register(Consumer<T> action) {
    this.action = action;
    if (this.value != null) {
      return () -> { action.accept(value); return true; };
    } else {
      return () -> { return false; };
    }
  }
}
