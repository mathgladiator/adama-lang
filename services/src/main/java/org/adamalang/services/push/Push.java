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
package org.adamalang.services.push;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;

import java.util.HashSet;
import java.util.function.Consumer;

public class Push extends SimpleService  {
  private final FirstPartyMetrics metrics;
  private final Pusher pusher;

  public Push(FirstPartyMetrics metrics, Pusher pusher) {
    super("push", new NtPrincipal("push", "service"), true);
    this.metrics = metrics;
    this.pusher = pusher;
  }

  public static String definition(int uniqueId, String params, HashSet<String> names, Consumer<String> error) {
    StringBuilder sb = new StringBuilder();
    sb.append("message _NotifyReq { string domain; dynamic payload; }\n");
    sb.append("message _PushResult { string push_id; }\n");
    sb.append("service push {\n");
    sb.append("  class=\"push\";\n");
    sb.append("  ").append(params).append("\n");
    sb.append("  method<_NotifyReq, _PushResult").append("> notify;\n");
    sb.append("}\n");
    return sb.toString();
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    switch (method) {
      case "notify": {
        String pushTrackToken = ProtectedUUID.generate();
        ObjectNode node = Json.parseJsonObject(request);
        ObjectNode payload = Json.readObject(node, "payload");
        payload.put("@pt", pushTrackToken);
        payload.put("@timestamp", LogTimestamp.now());
        payload.put("@agent", who.agent);
        payload.put("@authority", who.authority);
        String domain = Json.readString(node, "domain");
        pusher.notify(pushTrackToken, domain, who, payload.toString(), new Callback<>() {
          @Override
          public void success(String value) {
            ObjectNode response = Json.newJsonObject();
            response.put("push_id", value);
            callback.success(response.toString());
          }

          @Override
          public void failure(ErrorCodeException ex) {
            callback.failure(ex);
          }
        });
        return;
      }
      default:
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
  }
}
