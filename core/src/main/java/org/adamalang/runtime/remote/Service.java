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

import org.adamalang.runtime.natives.NtClient;

/** a service is responsible for executing a method with a message */
public interface Service {

  /**
   * @param agent who is executing the method
   * @param method the method being executed
   * @param message the input message (JSON)
   * @param callback a callback which returns either a failure
   */
  public void execute(NtClient agent, String method, String message, RemoteCallback callback);
}
