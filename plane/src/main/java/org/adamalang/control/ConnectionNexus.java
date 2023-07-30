/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.control;

import org.adamalang.common.SimpleExecutor;
import org.adamalang.web.io.JsonLogger;;

public class ConnectionNexus {
  public final JsonLogger logger;
  public final ApiMetrics metrics;
  public final SimpleExecutor executor;

  public ConnectionNexus(JsonLogger logger, ApiMetrics metrics, SimpleExecutor executor) {
    this.logger = logger;
    this.metrics = metrics;
    this.executor = executor;
  }
}
