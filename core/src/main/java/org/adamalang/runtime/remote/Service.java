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
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.NtResult;

import java.util.function.Function;

/** a service is responsible for executing a method with a message */
public interface Service {

  /** invoke the given method */
  public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtClient agent, NtMessageBase request, Function<String, T> result);

  public static final Service FAILURE = new Service() {
    @Override
    public <T> NtResult<T> invoke(Caller caller, String method, RxCache cache, NtClient agent, NtMessageBase request, Function<String, T> result) {
      return new NtResult<>(null, true, 500, "Service failed to resolve");
    }
  };
}
