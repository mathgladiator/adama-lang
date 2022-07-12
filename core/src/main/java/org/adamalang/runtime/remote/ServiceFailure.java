package org.adamalang.runtime.remote;

import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.NtResult;

import java.util.function.Function;

public class ServiceFailure implements Service {
  @Override
  public <T> NtResult<T> invoke(String method, RxCache cache, NtClient agent, NtMessageBase request, Function<String, T> result) {
    return new NtResult<>(null, true);
  }
}
