/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys;

import java.util.concurrent.atomic.AtomicBoolean;

/** a bunch of policies to influence behavior of the service during high load situation */
public class ServiceShield {
  public AtomicBoolean canConnectExisting;
  public AtomicBoolean canConnectNew;
  public AtomicBoolean canSendMessageExisting;

  public ServiceShield() {
    this.canConnectExisting = new AtomicBoolean(true);
    this.canConnectNew = new AtomicBoolean(true);
    this.canSendMessageExisting = new AtomicBoolean(true);
  }
}
