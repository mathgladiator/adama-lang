/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.natives.NtPrincipal;

/** represents a future which holds a value which */
public class SimpleFuture<T> {
  public final String channel;
  public final NtPrincipal who;
  private final T value;

  public SimpleFuture(final String channel, final NtPrincipal who, final T value) {
    this.channel = channel;
    this.who = who;
    this.value = value;
  }

  /** the code is asking for it now */
  public T await() throws ComputeBlockedException {
    if (value != null) {
      return value;
    }
    throw new ComputeBlockedException(who, channel);
  }

  /** does the value for the future exist in the moment at this time */
  public boolean exists() {
    return value != null;
  }
}
