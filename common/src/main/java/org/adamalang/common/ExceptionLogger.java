/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** allows exceptions to be monitored externally */
public interface ExceptionLogger {
  static ExceptionLogger FOR(Class<?> clazz) {
    Logger logger = LoggerFactory.getLogger(clazz);
    return (t, ec) -> {
      logger.error("exception", t);
    };
  }
  static ExceptionLogger FOR(Logger logger) {
    return (t, ec) -> {
      logger.error("exception", t);
    };
  }

  /**
   * an issue emerged which was not understood by an error code, and was returned to use as the
   * given error code
   */
  void convertedToErrorCode(Throwable t, int errorCode);
}
