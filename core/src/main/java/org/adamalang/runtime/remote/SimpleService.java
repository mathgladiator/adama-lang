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

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.NtResult;

import java.util.function.Function;

/** simplifies the connecting of the dots */
public abstract class SimpleService implements Service {
  private final String name;
  private final NtClient agent;
  private final boolean firstParty;

  public SimpleService(String name, NtClient agent, boolean firstParty) {
    this.name = name;
    this.agent = agent;
    this.firstParty = firstParty;
  }

  @Override
  public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtClient who, NtMessageBase request, Function<String, T> parser) {
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
