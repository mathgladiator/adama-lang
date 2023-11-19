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
package org.adamalang.services.video;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.keys.RSAPemKey;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.interfaces.RSAPrivateKey;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Jitsi extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Jitsi.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final RSAPrivateKey privateKey;
  private final String sub;
  private final SimpleExecutor offload;

  public Jitsi(FirstPartyMetrics metrics, WebClientBase base, SimpleExecutor offload, RSAPrivateKey privateKey, String sub) {
    super("jitsi", new NtPrincipal("jitsi", "service"), true);
    this.metrics = metrics;
    this.base = base;
    this.offload = offload;
    this.privateKey = privateKey;
    this.sub = sub;
  }

  public static Jitsi build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base, SimpleExecutor offload) throws Exception {
    return new Jitsi(metrics, base, offload, RSAPemKey.privateFrom(config.getDecryptedSecret("private_key")), config.getString("sub", "sub"));
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _JitsiSignRequest { string id; string name; string avatar; string email; bool moderator; bool hide; string room; }\n");
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
      case "signToken": {
        offload.execute(new NamedRunnable("jitsi-sign") {
          @Override
          public void execute() throws Exception {
            try {
              ObjectNode response = Json.newJsonObject();
              response.put("jwt", tokenize(privateKey, sub, requestNode));
              callback.success(response.toString());
            } catch (Exception ex) {
              LOGGER.error("jitzi-fail", ex);
              callback.failure(new ErrorCodeException(ErrorCodes.JITSI_FAILED_SIGNING_TOKEN));
            }
          }
        });
        return;
      }
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }

  private static final String[] FIELDS_TO_COPY_USER_AS_STR = new String[] {"id", "name", "avatar", "email"};

  private static String tokenize(RSAPrivateKey privateKey, String sub, ObjectNode requestNode) {
    LinkedHashMap<String, Object> claims = new LinkedHashMap<>();
    claims.put("aud", "jitsi");
    claims.put("iss", "chat");
    claims.put("iat", System.currentTimeMillis() / 1000);
    claims.put("exp", System.currentTimeMillis() / 1000 + 60 * 60 * 24 * 7);
    claims.put("nbf", System.currentTimeMillis() / 1000 - 60 * 60 * 24);
    claims.put("sub", sub.substring(0, sub.indexOf('/')));
    {
      TreeMap<String, Object> context = new TreeMap<>();
      TreeMap<String, Object> user = new TreeMap<>();
      for (String f : FIELDS_TO_COPY_USER_AS_STR) {
        user.put(f, requestNode.get(f).textValue());
      }
      if (requestNode.has("hide") && requestNode.get("hide").booleanValue()) {
        user.put("hidden-from-recorder", "true");
      }
      if (requestNode.has("moderator") && requestNode.get("moderator").booleanValue()) {
        user.put("moderator", "true");
      }
      context.put("user", user);
      TreeMap<String, Object> features = new TreeMap<>();
      features.put("livestreaming", "false");
      features.put("outbound-call", "false");
      features.put("transcription", "false");
      features.put("recording", "false");
      features.put("sip-outbound-call", "false");
      context.put("features", features);
      claims.put("context", context);
    }
    claims.put("room", requestNode.get("room").textValue());
    return Jwts.builder().setClaims(claims).setHeaderParam("kid", sub).setHeaderParam("typ", "JWT").signWith(privateKey).compact();
  }
}
