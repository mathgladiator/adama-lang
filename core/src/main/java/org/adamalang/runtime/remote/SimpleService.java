/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtResult;
import org.adamalang.runtime.natives.NtToDynamic;

import java.util.function.Function;

/** simplifies the connecting of the dots */
public abstract class SimpleService implements Service {
  private final String name;
  private final NtPrincipal agent;
  private final boolean firstParty;

  public SimpleService(String name, NtPrincipal agent, boolean firstParty) {
    this.name = name;
    this.agent = agent;
    this.firstParty = firstParty;
  }

  @Override
  public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtPrincipal who, NtToDynamic request, Function<String, T> parser) {
    return cache.answer(name, method, who, request, parser, (id, json) -> {
      request(method, json, new Callback<String>() {
        @Override
        public void success(String value) {
          deliver(new RemoteResult(value, null, null));
        }

        @Override
        public void failure(ErrorCodeException ex) {
          deliver(new RemoteResult(null, "" + ex.getMessage(), ex.code));
        }

        public void deliver(RemoteResult result) {
          Key key = new Key(caller.__getSpace(), caller.__getKey());
          caller.__getDeliverer().deliver(agent, key, id, result, firstParty, Callback.DONT_CARE_INTEGER);
        }
      });
    });
  }

  public abstract void request(String method, String request, Callback<String> callback);
}
