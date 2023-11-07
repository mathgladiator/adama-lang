package org.adamalang.services.video;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.services.social.Discord;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.function.Consumer;

public class Jitsi extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Jitsi.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;

  public Jitsi(FirstPartyMetrics metrics, WebClientBase base) {
    super("jitsi", new NtPrincipal("jitsi", "service"), true);
    this.metrics = metrics;
    this.base = base;
  }

  public static Jitsi build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    return new Jitsi(metrics, base);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _JitsiSignRequest { string id; string name; string avatar; string email; bool moderator; bool hide; String room; }\n");
    sb.append("message _JitsiSignResponse { string jwt; }\n");
    sb.append("service jitsi {\n");
    sb.append("  class=\"jitsi\";\n");
    if (!names.contains("private_key")) {
      error.accept("private_key is required and it should be encrypted");
    }
    if (!names.contains("sub")) {
      error.accept("sub is required");
    }
    sb.append("  ").append(params).append("\n");
    sb.append("  method<_JitsiSignRequest, _JitsiSignResponse> signToken;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode requestNode = Json.parseJsonObject(request);
    switch (method) {
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
