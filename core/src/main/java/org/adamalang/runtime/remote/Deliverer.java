/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtPrincipal;

/** how results are delivered from services */
public interface Deliverer {

  Deliverer FAILURE = new Deliverer() {
    @Override
    public void deliver(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback) {
      callback.failure(new ErrorCodeException(ErrorCodes.DELIVERER_FAILURE_NOT_SET));
    }
  };

  void deliver(NtPrincipal agent, Key key, int id, RemoteResult result, boolean firstParty, Callback<Integer> callback);
}
