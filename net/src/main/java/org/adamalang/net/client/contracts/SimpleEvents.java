/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.net.client.contracts;

public interface SimpleEvents {
  /** the connection was successful, and we can talk to the document via the remote */
  void connected();

  /** a data change has occurred */
  void delta(String data);

  /** an error has occurred */
  void error(int code);

  /** the document was disconnected */
  void disconnected();
}
