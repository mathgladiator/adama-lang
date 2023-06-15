package org.adamalang.common;

/** A simple streaming interface */
public interface Stream<T> {
  /** data was produced */
  void next(T value);

  /** the stream is finished */
  void complete();

  /** the action failed outright, and the reason is the exception */
  void failure(ErrorCodeException ex);
}
