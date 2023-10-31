package org.adamalang.services.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.ExceptionLogger;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.SimpleHttpRequestBody;
import org.adamalang.web.client.StringCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;

import java.util.HashMap;


public class GithubValidator extends SimpleService {
  private static final Logger LOG = LoggerFactory.getLogger(GithubValidator.class);
  private static final ExceptionLogger EXLOGGER = ExceptionLogger.FOR(LOG);
  private final FirstPartyMetrics metrics;
  private final WebClientBase webClientBase;

  private GithubValidator(FirstPartyMetrics metrics, WebClientBase webClientBase) {
    super("githubvalidator", new NtPrincipal("githubvalidator", "service"), true);
    this.metrics = metrics;
    this.webClientBase = webClientBase;
  }

  public static GithubValidator build(FirstPartyMetrics metrics, WebClientBase webClientBase) {
    return new GithubValidator(metrics, webClientBase);
  }

  public static String definition() {
    // The definition structure can be similar to GoogleValidator's definition, adjusted for GitHub.
    StringBuilder sb = new StringBuilder();
    sb.append("message _GithubValidate_Req { string token; }\n");
    sb.append("message _GithubValidate_Result { string login; maybe<string> name; maybe<string> email; maybe<string> avatar; }\n");
    sb.append("service githubvalidator {\n");
    sb.append("  class=\"githubvalidator\";\n");
    sb.append("  method<_GithubValidate_Req, _GithubValidate_Result> validate;\n");
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
      SimpleHttpRequest get = new SimpleHttpRequest("GET", "https://api.github.com/user", headers, SimpleHttpRequestBody.EMPTY);

      webClientBase.execute(get, new StringCallbackHttpResponder(LOG, metrics.github_validate.start(), new Callback<String>() {
        @Override
        public void success(String value) {
          try {
            ObjectNode githubProfile = Json.parseJsonObject(value);
            String login = Json.readString(githubProfile, "login");

            if (login == null) {
              callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_GITHUB_MISSING_LOGIN));
              return;
            }

            String name = Json.readString(githubProfile, "name");
            String email = Json.readString(githubProfile, "email");
            String avatar = Json.readString(githubProfile, "avatar_url");

            ObjectNode result = Json.newJsonObject();
            result.put("login", login);
            if (name != null) { result.put("name", name); }
            if (email != null) { result.put("email", email); }
            if (avatar != null) { result.put("avatar", avatar); }

            callback.success(result.toString());
          } catch (Exception ex) {
            callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_GITHUB_PARSE_FAILURE, ex, EXLOGGER));
          }
        }

        @Override
        public void failure(ErrorCodeException ex) {
          callback.failure(ex);
        }
      }));

    } catch (Exception ex) {
      callback.failure(ErrorCodeException.detectOrWrap(ErrorCodes.FIRST_PARTY_GITHUB_UNKNOWN_FAILURE, ex, EXLOGGER));
    }
  }
}