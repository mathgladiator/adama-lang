/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
