/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.common.net;

import org.adamalang.common.ErrorCodeException;

/** each connection has a separate lifecycle from birth to death */
public interface Lifecycle {
  /** the connection was created and achieved glorious success */
  void connected(ChannelClient channel);

  /** the connection failed to connect */
  void failed(ErrorCodeException ex);

  /** the successful connection was later disconnected */
  void disconnected();
}
