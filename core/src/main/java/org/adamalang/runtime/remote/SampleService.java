/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.remote;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtPrincipal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

/** this is a sample service */
public class SampleService extends SimpleService {
  public SampleService() {
    super("sample", new NtPrincipal("sample", "services"), true);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message EchoRequestAndResponse_").append(uniqueId).append(" { string message; }\n");
    sb.append("service sample {\n");
    sb.append("  class=\"sample\";\n");
    sb.append("  ").append(params).append("\n");
    if (names.contains("error")) {
      error.accept("has an error param which is an error");
    }
    sb.append("  method<EchoRequestAndResponse_").append(uniqueId).append(", EchoRequestAndResponse_").append(uniqueId).append("> echo;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(String method, String request, Callback<String> callback) {
    if (method.equals("echo")) {
      callback.success(request);
    } else {
      callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
