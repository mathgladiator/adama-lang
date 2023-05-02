/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.metrics;

/** record success/failure rates, latency, failure codes */
public interface RequestResponseMonitor {

  /** start an operation */
  RequestResponseMonitorInstance start();

  /** the operation must success or fail. We also capture common bugs */
  interface RequestResponseMonitorInstance {
    void success();

    void extra();

    void failure(int code);
  }
}
