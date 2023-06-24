/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
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
