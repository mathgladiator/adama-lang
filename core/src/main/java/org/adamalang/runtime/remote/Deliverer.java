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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtClient;

/** how results are delivered from services */
public interface Deliverer {

  public void deliver(NtClient agent, Key key, int id, RemoteResult result, Callback<Integer> callback);

  public static final Deliverer FAILURE = new Deliverer() {
    @Override
    public void deliver(NtClient agent, Key key, int id, RemoteResult result, Callback<Integer> callback) {
      callback.failure(new ErrorCodeException(ErrorCodes.DELIVERER_FAILURE_NOT_SET));
    }
  };
}
