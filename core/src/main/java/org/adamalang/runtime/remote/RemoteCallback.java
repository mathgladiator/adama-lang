/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.remote;

/** A callback for a remote service */
public interface RemoteCallback {
  /** the remote call was successful; the result is a json object or array */
  public void success(String result);

  /** the remote call was a failure; the code is useful for code, and the message is useful for humans */
  public void failure(int code, String message);
}
