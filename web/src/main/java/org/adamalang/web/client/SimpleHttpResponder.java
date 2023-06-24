/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.client;

import org.adamalang.common.ErrorCodeException;

/** Simple http responder which receives */
public interface SimpleHttpResponder {
  /** the response has come in */
  void start(SimpleHttpResponseHeader header);

  /** start the body */
  void bodyStart(long size);

  /** provide a body fragment */
  void bodyFragment(byte[] chunk, int offset, int len);

  /** end the body with a digest */
  void bodyEnd();

  /** a failure occured */
  void failure(ErrorCodeException ex);
}
