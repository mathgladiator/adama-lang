/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.io;

import org.adamalang.common.ErrorCodeException;

/** a simple interface for responding to JsonRequest */
public interface JsonResponder {
  /** stream an update */
  void stream(String json);

  /** respond in a terminal fashion */
  void finish(String json);

  /** respond with a terminal error */
  void error(ErrorCodeException ex);
}
