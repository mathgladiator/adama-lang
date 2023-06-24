/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.extern;

/** Some behaviors are held in reserve for security, and communication is decoupled using a queue */
public interface SignalControl {

  /** tell the controller that a new domain requires automatic certificate registration */
  public void raiseAutomaticDomain(String domain);
}
