/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

/** This is useful for knowing if we recently started up */
public class StartUp {
  private static final long STARTED = System.currentTimeMillis();

  public static boolean hasRecentlyStartedUp() {
    return (System.currentTimeMillis() - STARTED) < 30000;
  }
}
