/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.exceptions;

/** the document throws this to rewind itself; this is destructive of all state */
public class PerformDocumentRewindException extends RuntimeException {
  public final int seq;

  /** @param seq the sequencer to rewind to. */
  public PerformDocumentRewindException(int seq) {
    this.seq = seq;
  }
}
