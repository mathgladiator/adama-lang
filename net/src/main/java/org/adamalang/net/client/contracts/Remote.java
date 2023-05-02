/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.contracts;

import org.adamalang.common.Callback;

public interface Remote {
  void canAttach(Callback<Boolean> callback);

  void attach(String id, String name, String contentType, long size, String md5, String sha384, Callback<Integer> callback);

  void send(String channel, String marker, String message, Callback<Integer> callback);

  void update(String viewerState);

  void disconnect();
}
