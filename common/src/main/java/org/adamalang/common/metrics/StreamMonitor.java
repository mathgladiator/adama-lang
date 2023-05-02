/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.metrics;

/** record tim until first data, length of stream, failure rates, codes */
public interface StreamMonitor {

  /** start an operation */
  StreamMonitorInstance start();

  /** the operation makes progress, finishes or fails */
  interface StreamMonitorInstance {
    void progress();

    void finish();

    void failure(int code);
  }
}
