/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.common.metrics;

/** record tim until first data, length of stream, failure rates, codes */
public interface StreamMonitor {

  /** start an operation */
  public StreamMonitorInstance start();

  /** the operation makes progress, finishes or fails */
  public interface StreamMonitorInstance {
    public void progress();

    public void finish();

    public void failure(int code);
  }
}
