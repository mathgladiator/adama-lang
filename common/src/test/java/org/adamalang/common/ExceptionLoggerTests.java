/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.common;

import org.junit.Test;
import org.slf4j.LoggerFactory;

public class ExceptionLoggerTests {
  @Test
  public void flow() {
    ExceptionLogger.FOR(ExceptionLogger.class).convertedToErrorCode(new NullPointerException(), -1);
    ExceptionLogger.FOR(LoggerFactory.getLogger(ExceptionLogger.class)).convertedToErrorCode(new NullPointerException(), -1);
  }
}
