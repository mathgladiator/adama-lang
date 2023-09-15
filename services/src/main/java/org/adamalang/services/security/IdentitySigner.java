/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.services.security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.jsonwebtoken.Jwts;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.runtime.security.Keystore;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;

import java.security.PrivateKey;
import java.util.HashSet;
import java.util.function.Consumer;

public class IdentitySigner extends SimpleService {
  private final FirstPartyMetrics metrics;
  private final SimpleExecutor offload;
  private final String authority;
  private final PrivateKey privateKey;

  public IdentitySigner(FirstPartyMetrics metrics, SimpleExecutor offload, String authority, String privateKey) throws ErrorCodeException {
    super("identitysigner", new NtPrincipal("identitysigner", "service"), true);
    this.metrics = metrics;
    this.offload = offload;
    this.authority = authority;
    this.privateKey = Keystore.parsePrivateKey(Json.parseJsonObject(privateKey));
  }

  public static IdentitySigner build(FirstPartyMetrics metrics, ServiceConfig config, SimpleExecutor offload) throws ErrorCodeException {
    String privateKey = config.getDecryptedSecret("private_key");
    String authority = config.getString("authority", "");
    return new IdentitySigner(metrics, offload, authority, privateKey);
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _IdentitySigner_Req").append(" { string agent; }\n");
    sb.append("message _IdentitySigner_Res").append(" { string identity; }\n");
    sb.append("service identitysigner {\n");
    sb.append("  class=\"identitysigner\";\n");
    sb.append("  ").append(params).append("\n");
    if (!names.contains("authority")) {
      error.accept("identitysigner requires an 'authority' field");
    }
    if (!names.contains("private_key")) {
      error.accept("identitysigner requires a 'private_key' field (and it should be encrypted)");
    }
    sb.append("  method<_IdentitySigner_Req").append(", _IdentitySigner_Res").append("> sign;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    offload.execute(new NamedRunnable("off-main") {
      @Override
      public void execute() throws Exception {
        try {
          if ("sign".equals(method)) {
            ObjectNode parsed = Json.parseJsonObject(request);
            String agent = parsed.get("agent").textValue();
            String identity = Jwts.builder().setSubject(agent).setIssuer(authority).signWith(privateKey).compact();
            callback.success("{\"identity\":\"" + identity + "\"}");
          } else {
            callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
          }
        } catch (Exception ex) {
          callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_EXCEPTION));
        }
      }
    });
  }
}
