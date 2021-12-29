/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.runtime.async;

import org.adamalang.runtime.exceptions.ComputeBlockedException;
import org.adamalang.runtime.natives.NtClient;

/** represents a future which holds a value which */
public class SimpleFuture<T> {
  private final String channel;
  private final T value;
  private final NtClient who;

  public SimpleFuture(final String channel, final NtClient who, final T value) {
    this.channel = channel;
    this.who = who;
    this.value = value;
  }

  /** the code is asking for it now */
  public T await() throws ComputeBlockedException {
    if (value != null) { return value; }
    throw new ComputeBlockedException(who, channel);
  }

  /** does the value for the future exist in the moment at this time */
  public boolean exists() {
    return value != null;
  }
}
