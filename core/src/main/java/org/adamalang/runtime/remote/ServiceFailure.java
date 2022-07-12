package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtClient;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.NtResult;

import java.util.function.Function;

/** a dumb implementation of a service that only failes */
public class ServiceFailure implements Service {
  @Override
  public <T> NtResult<T> invoke(String method, RxCache cache, NtClient agent, NtMessageBase request, Function<String, T> result) {
    JsonStreamWriter writer = new JsonStreamWriter();
    request.__writeOut(writer);
    RemoteInvocation invocation = new RemoteInvocation("service_failure", method, agent, request.toString());
    return new NtResult<>(null, true);
  }
}
