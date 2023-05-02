/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.exceptions;

import org.adamalang.runtime.natives.NtPrincipal;

/** the compute was blocked, and we must wait for data from outside */
public class ComputeBlockedException extends RuntimeException {
  public final String channel;
  public final NtPrincipal client;

  public ComputeBlockedException(final NtPrincipal client, final String channel) {
    this.client = client;
    this.channel = channel;
  }

  public ComputeBlockedException() {
    this.client = null;
    this.channel = null;
  }
}
