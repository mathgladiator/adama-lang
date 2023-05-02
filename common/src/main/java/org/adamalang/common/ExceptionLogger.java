/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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
