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
package org.adamalang.services.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.StringCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Consumer;

public class GoogleValidator extends SimpleService  {
  private static final Logger LOG = LoggerFactory.getLogger(GoogleValidator.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOG);
  private final FirstPartyMetrics metrics;
  private final SimpleExecutor offload;
  private final WebClientBase webClientBase;

  private GoogleValidator(FirstPartyMetrics metrics, SimpleExecutor offload, WebClientBase webClientBase) {
    super("googlevalidator", new NtPrincipal("googlevalidator", "service"), true);
    this.metrics = metrics;
    this.offload = offload;
    this.webClientBase = webClientBase;
  }

  public static GoogleValidator build(FirstPartyMetrics metrics, SimpleExecutor offload, WebClientBase webClientBase) {
    return new GoogleValidator(metrics, offload, webClientBase);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _GoogleValidate_Req").append(" { string token; }\n");
    sb.append("message _GoogleValidate_Result").append(" { string email; maybe<string> name; maybe<string> picture; }\n");
    sb.append("service googlevalidator {\n");
    sb.append("  class=\"googlevalidator\";\n");
    sb.append("  method<_GoogleValidate_Req").append(", _GoogleValidate_Result").append("> validate;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    try {
      ObjectNode requestNode = Json.parseJsonObject(request);
      String token = Json.readString(requestNode, "token");
      HashMap<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Bearer " + token);
      SimpleHttpRequest get = new SimpleHttpRequest("GET", "https://www.googleapis.com/oauth2/v1/userinfo", headers, SimpleHttpRequestBody.EMPTY);
      webClientBase.execute(get, new StringCallbackHttpResponder(LOG, metrics.google_validate.start(), new Callback<String>() {
        @Override
        public void success(String value) {
          try {
            ObjectNode googleProfile = Json.parseJsonObject(value);
            String email = Json.readString(googleProfile, "email");
            if (email == null) {
              callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_GOOGLE_MISSING_EMAIL));
              return;
            }
            String name = Json.readString(googleProfile, "name");
            String picture = Json.readString(googleProfile, "picture");
            ObjectNode filteredResult = Json.newJsonObject();
            filteredResult.put("email", email);
            if (name != null) {
              filteredResult.put("name", name);
            }
            if (picture != null) {
              filteredResult.put("picture", picture);
            }
            callback.success(filteredResult.toString());
          } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_GOOGLE_UNKNOWN_FAILURE, ex, EXLOGGER));
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      }));
    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_GOOGLE_UNKNOWN_FAILURE, ex, EXLOGGER));
    }
  }
}
