/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AwaitHelper {

  public static boolean block(CountDownLatch latch, int ms) {
    try {
      return latch.await(ms, TimeUnit.MILLISECONDS);
    } catch (InterruptedException ie) {
      return false;
    }
  }
}
