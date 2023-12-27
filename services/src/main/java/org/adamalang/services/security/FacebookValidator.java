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

import org.adamalang.common.Callback;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.metrics.FirstPartyMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.adamalang.web.client.WebClientBase;

public class FacebookValidator extends SimpleService {
  private static final Logger LOG = LoggerFactory.getLogger(FacebookValidator.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOG);
  private final FirstPartyMetrics metrics;
  private final WebClientBase webClientBase;

  private FacebookValidator(FirstPartyMetrics metrics, WebClientBase webClientBase) {
    super("facebookvalidator", new NtPrincipal("facebookvalidator", "service"), true);
    this.metrics = metrics;
    this.webClientBase = webClientBase;
  }

  public static FacebookValidator build(FirstPartyMetrics metrics, WebClientBase webClientBase) {
    return new FacebookValidator(metrics, webClientBase);
  }

  public static String definition() {
    StringBuilder sb = new StringBuilder();
    sb.append("message _FacebookValidate_Req { string token; }\n");
    sb.append("message _FacebookValidate_Result { string id; maybe<string> name; maybe<string> email; maybe<string> picture; }\n");
    sb.append("service facebookvalidator {\n");
    sb.append("  class=\"facebookvalidator\";\n");
    sb.append("  method<_FacebookValidate_Req, _FacebookValidate_Result> validate;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    /*
    try {
      ObjectNode requestNode = Json.parseJsonObject(request);
      String token = Json.readString(requestNode, "token");

      HashMap<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Bearer " + token);
      SimpleHttpRequest get = new SimpleHttpRequest("GET", "https://graph.facebook.com/me?fields=id,name,email,picture", headers, SimpleHttpRequestBody.EMPTY);

      webClientBase.execute(get, new StringCallbackHttpResponder(LOG, metrics.facebook_validate.start(), new Callback<String>() {
        @Override
        public void success(String value) {
          try {
            ObjectNode facebookProfile = Json.parseJsonObject(value);
            String id = Json.readString(facebookProfile, "id");

            if (id == null) {
              callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_FACEBOOK_MISSING_ID));
              return;
            }

            String name = Json.readString(facebookProfile, "name");
            String email = Json.readString(facebookProfile, "email");
            // Parsing picture requires an extra step due to the Facebook API's structure.
            ObjectNode pictureNode = Json.readObject(facebookProfile, "picture");
            String pictureURL = pictureNode != null ? Json.readString(pictureNode.path("data"), "url") : null;

            ObjectNode result = Json.newJsonObject();
            result.put("id", id);
            if (name != null) { result.put("name", name); }
            if (email != null) { result.put("email", email); }
            if (pictureURL != null) { result.put("picture", pictureURL); }

            callback.success(result.toString());
          } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_FACEBOOK_PARSE_FAILURE, ex, EXLOGGER));
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      }));

    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_FACEBOOK_UNKNOWN_FAILURE, ex, EXLOGGER));
    }
    */
  }
}
