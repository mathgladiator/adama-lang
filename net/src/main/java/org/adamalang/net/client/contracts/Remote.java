/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.Callback;

public interface Remote {
  void canAttach(AskAttachmentCallback callback);

  void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback);

  void send(String channel, String marker, String message, Callback<Integer> callback);

  void update(String viewerState);

  void disconnect();
}
