/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.services.sms;

/*
import com.twilio.http.TwilioRestClient;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.ServiceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
*/

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceConfig;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** https://www.twilio.com/docs/api */
public class Twilio extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Twilio.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;
  private final String username;
  private final String password;

  public Twilio(FirstPartyMetrics metrics, WebClientBase base, String username, String password) {
    super("twilio", new NtPrincipal("twilio", "service"), true);
    this.metrics = metrics;
    this.base = base;
    this.username = username;
    this.password = password;
  }

  public static Twilio build(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    String username = config.getDecryptedSecret("username");
    String password = config.getDecryptedSecret("password");
    return new Twilio(metrics, base, username, password);
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    if ("send".equals(method)) {
      callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED));
      return;
    } else {
      callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_FOUND));
    }
    /*
    if ("send".equals(method)) {
      String _from = null;
      String _to = null;
      String _message = null;
      JsonStreamReader reader = new JsonStreamReader(request);
      if (reader.startObject()) {
        while (reader.notEndOfObject()) {
          switch (reader.fieldName()) {
            case "from":
            case "From":
              _from = reader.readString();
              break;
            case "to":
            case "To":
              _to = reader.readString();
              break;
            case "message":
            case "msg":
            case "body":
            case "Message":
              _message = reader.readString();
              break;
          }
        }
      }
      String from = _from;
      String to = _to;
      String message = _message;
      if (from == null || to == null || message == null) {
        callback.failure(new ErrorCodeException(500, "Either from, to, or message is not set"));
        return;
      }
      executor.execute(() -> {
        try {
          Message result = Message.creator(new PhoneNumber(from), new PhoneNumber(to), message).create(this.client);
          JsonStreamWriter writer = new JsonStreamWriter();
          writer.beginObject();
          writer.writeObjectFieldIntro("sid");
          writer.writeString(result.getSid());
          writer.endObject();
          callback.success(writer.toString());
        } catch (Exception ex) {
          // TODO: Break down exception into things
          LOGGER.error("twilio-exception", ex);
          callback.failure(new ErrorCodeException(400, ex));
        }
      });
    }
    */
  }
}
