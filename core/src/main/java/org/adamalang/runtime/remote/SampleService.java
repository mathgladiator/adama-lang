/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.runtime.remote;

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.natives.NtPrincipal;

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
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    if (method.equals("echo")) {
      callback.success(request);
    } else {
      callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
