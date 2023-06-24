/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.mocks;

import org.adamalang.common.ExceptionLogger;

public class StdErrLogger implements ExceptionLogger {
  @Override
  public void convertedToErrorCode(Throwable t, int errorCode) {
    System.err.println("ERROR:" + errorCode);
    t.printStackTrace(System.err);
  }
}
