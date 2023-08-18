/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.deploy;

/** contract for deployment synchronization */
public interface DeploySync {
  /** watch the given space for deployments */
  public void watch(String space);

  /** stop watching the space for deployments */
  public void unwatch(String space);
}
