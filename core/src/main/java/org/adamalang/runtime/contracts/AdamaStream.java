/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.contracts;

import org.adamalang.common.Callback;

/** interface for a signel Adama stream/connection to a single document */
public interface AdamaStream {

  /** update the viewer state */
  void update(String newViewerState);

  /** send the document a message on the given channel */
  void send(String channel, String marker, String message, Callback<Integer> callback);

  /** can attachments be made with the owner of the connection */
  void canAttach(Callback<Boolean> callback);

  /** attach an asset to the stream */
  void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback);

  /** close the stream */
  void close();
}
