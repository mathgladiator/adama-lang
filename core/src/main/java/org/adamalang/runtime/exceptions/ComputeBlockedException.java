/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.exceptions;

import org.adamalang.runtime.natives.NtClient;

/** the compute was blocked, and we must wait for data from outside */
public class ComputeBlockedException extends RuntimeException {
  public final String channel;
  public final NtClient client;

  public ComputeBlockedException(final NtClient client, final String channel) {
    this.client = client;
    this.channel = channel;
  }

  public ComputeBlockedException() {
    this.client = null;
    this.channel = null;
  }
}
