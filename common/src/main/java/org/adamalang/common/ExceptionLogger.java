/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
