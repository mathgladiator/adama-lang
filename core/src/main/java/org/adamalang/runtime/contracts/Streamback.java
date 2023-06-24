/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.sys.CoreStream;

/** This is like a callback, but for an infinite stream. */
public interface Streamback {
  /** the stream has been setup and can be interacted with via the core stream */
  void onSetupComplete(CoreStream stream);

  /** inform the client of a status update */
  void status(StreamStatus status);

  /** inform the client of new data */
  void next(String data);

  /** inform the client that a failure has occurred */
  void failure(ErrorCodeException exception);

  /** the stream has a status representing what is happening at the given moment */
  enum StreamStatus {
    Connected, Disconnected
  }
}
