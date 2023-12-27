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

public class TwitterValidator extends SimpleService {
    private static final Logger LOG = LoggerFactory.getLogger(TwitterValidator.class);
    private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOG);
    private final FirstPartyMetrics metrics;
    private final WebClientBase webClientBase;

    private TwitterValidator(FirstPartyMetrics metrics, WebClientBase webClientBase) {
        super("twittervalidator", new NtPrincipal("twittervalidator", "service"), true);
        this.metrics = metrics;
        this.webClientBase = webClientBase;
    }

    public static TwitterValidator build(FirstPartyMetrics metrics, WebClientBase webClientBase) {
        return new TwitterValidator(metrics, webClientBase);
    }

    public static String definition() {
        StringBuilder sb = new StringBuilder();
        sb.append("message _TwitterValidate_Req { string token; }\n");
        sb.append("message _TwitterValidate_Result { string id; maybe<string> name; maybe<string> screen_name; maybe<string> profile_image_url_https; }\n");
        sb.append("service twittervalidator {\n");
        sb.append("  class=\"twittervalidator\";\n");
        sb.append("  method<_TwitterValidate_Req, _TwitterValidate_Result> validate;\n");
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
            SimpleHttpRequest get = new SimpleHttpRequest("GET", "https://api.twitter.com/1.1/account/verify_credentials.json", headers, SimpleHttpRequestBody.EMPTY);

            webClientBase.execute(get, new StringCallbackHttpResponder(LOG, metrics.twitter_validate.start(), new Callback<String>() {
                @Override
                public void success(String value) {
                    try {
                        ObjectNode twitterProfile = Json.parseJsonObject(value);
                        String id = Json.readString(twitterProfile, "id_str");

                        if (id == null) {
                            callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_TwITTER_MISSING_ID));
                            return;
                        }

                        String name = Json.readString(twitterProfile, "name");
                        String screen_name = Json.readString(twitterProfile, "screen_name");
                        String profile_image_url_https = Json.readString(twitterProfile, "profile_image_url_https");

                        ObjectNode result = Json.newJsonObject();
                        result.put("id", id);
                        if (name != null) { result.put("name", name); }
                        if (screen_name != null) { result.put("screen_name", screen_name); }
                        if (profile_image_url_https != null) { result.put("profile_image_url_https", profile_image_url_https); }

                        callback.success(result.toString());
                    } catch (Exception ex) {
                        callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_TWITTER_PARSE_FAILURE, ex, EXLOGGER));
                    }
                }

                @Override
                public void failure(ErrorCodeException ex) {
                    callback.failure(ex);
                }
            }));

        } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_TWITTER_UNKNOWN_FAILURE, ex, EXLOGGER));
        }
        */
    }

}
