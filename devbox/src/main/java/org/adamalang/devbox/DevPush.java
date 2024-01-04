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
package org.adamalang.devbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.Json;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.common.metrics.NoOpMetricsFactory;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.services.push.Push;
import org.adamalang.services.push.Pusher;
import org.adamalang.services.push.webpush.Subscription;
import org.adamalang.services.push.webpush.WebPushRequestFactory128;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.VoidCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.Iterator;

public class DevPush implements Pusher {
  private final static Logger LOGGER = LoggerFactory.getLogger(Pusher.class);
  public final File path;
  private final TerminalIO io;
  private final VAPIDPublicPrivateKeyPair keypair;
  private final WebClientBase webClientBase;
  private final WebPushRequestFactory128 webPushFactory128;

  public DevPush(TerminalIO io, File path, String email, VAPIDPublicPrivateKeyPair keypair, WebClientBase webClientBase) {
    this.io = io;
    this.path = path;
    this.keypair = keypair;
    this.webClientBase = webClientBase;
    this.webPushFactory128 = new WebPushRequestFactory128(email, new SecureRandom());
  }

  public void register(NtPrincipal who, String domain, ObjectNode subscription, ObjectNode deviceInfo) {
    try {
      ObjectNode push = load();
      ArrayNode subscriptions = null;
      if (push.has(domain)) {
        subscriptions = (ArrayNode) push.get(domain);
      } else {
        subscriptions = push.putArray(domain);
      }
      ObjectNode sub = subscriptions.addObject();
      sub.put("agent", who.agent);
      sub.put("authority", who.authority);
      sub.set("subscription", subscription);
      sub.set("device-info", deviceInfo);
      Files.writeString(path.toPath(), push.toPrettyString());
    } catch (Exception ex) {
      LOGGER.error("push-register-failure", ex);
    }
  }

  public ObjectNode load() throws Exception {
    if (path.exists()) {
      return Json.parseJsonObject(Files.readString(path.toPath()));
    } else {
      return Json.newJsonObject();
    }
  }

  @Override
  public void notify(String pushTrackToken, String domain, NtPrincipal who, String payload, Callback<Void> callback) {
    io.notice("devpush|" + pushTrackToken + " to " + who.agent + "@" + who.authority + " : " + payload);
    try {
      ObjectNode push = load();
      ArrayNode subscriptions = (ArrayNode) push.get(domain);
      Iterator<JsonNode> it = subscriptions.iterator();
      while (it.hasNext()) {
        JsonNode node = it.next();
        if (who.agent.equals(node.get("agent").textValue()) && who.authority.equals(node.get("authority").textValue())) {
          push(new Subscription(Json.parseJsonObject(node.get("subscription").toString())), payload);
        }
      }
      callback.success(null);
    } catch (Exception ex) {
      LOGGER.error("push-notify-failure", ex);
      callback.failure(new ErrorCodeException(-5000));
    }
  }

  public void push(Subscription subscription, String payload) {
    if (keypair == null) {
      LOGGER.error("a-push-attempt-was-made-without-valid-vapid-keys");
      return;
    }
    try {
      SimpleHttpRequest request = webPushFactory128.make(keypair, subscription, 14, payload.getBytes(StandardCharsets.UTF_8));
      webClientBase.executeShared(request, new VoidCallbackHttpResponder(LOGGER, new FirstPartyMetrics(new NoOpMetricsFactory()).webpush_send.start(), new Callback<Void>() {
        @Override
        public void success(Void value) {
          // track some metrics
        }

        @Override
        public void failure(ErrorCodeException ex) {
          // track some metrics
          if (ex.code == 410) {
            // TODO: remove the subscription
          }
        }
      }));
    } catch (Exception ex) {
      LOGGER.error("failed-push", ex);
    }
  }

  public void install() {
    ServiceRegistry.add("push", Push.class, (space, configRaw, keys) -> new Push(new FirstPartyMetrics(new NoOpMetricsFactory()), DevPush.this));
  }
}
