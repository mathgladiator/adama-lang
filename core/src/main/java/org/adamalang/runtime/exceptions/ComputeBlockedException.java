/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
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
}