/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.SimpleService;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

/** https://www.twilio.com/docs/api */
public class Twilio extends SimpleService {
  private static final Logger LOGGER = LoggerFactory.getLogger(Twilio.class);
  private final FirstPartyMetrics metrics;
  private final WebClientBase base;

  public Twilio(FirstPartyMetrics metrics, ServiceConfig config, WebClientBase base) throws ErrorCodeException {
    super("twilio", new NtPrincipal("twilio", "service"), true);
    this.metrics = metrics;
    this.base = base;
    String username = config.getDecryptedSecret("username");
    String password = config.getDecryptedSecret("password");
  }

  @Override
  public void request(NtPrincipal who, String method, String request, Callback<String> callback) {
    ObjectNode node = Json.parseJsonObject(request);
    switch (method) {
      case "send":
        callback.failure(new ErrorCodeException(ErrorCodes.FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED));
        return;
      default:
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
