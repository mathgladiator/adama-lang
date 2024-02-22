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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.auth.oauth2.GoogleCredentials;
import org.adamalang.ErrorCodes;
import org.adamalang.common.*;
import org.adamalang.common.keys.MasterKey;
import org.adamalang.common.keys.VAPIDFactory;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DeviceSubscription;
import org.adamalang.mysql.model.Domains;
import org.adamalang.mysql.model.PushSubscriptions;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.metrics.FirstPartyMetrics;
import org.adamalang.services.push.webpush.Subscription;
import org.adamalang.services.push.webpush.WebPushRequestFactory128;
import org.adamalang.web.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class GlobalPusher implements Pusher {
  private final static Logger LOGGER = LoggerFactory.getLogger(GlobalPusher.class);

  private final FirstPartyMetrics metrics;
  private final DataBase dataBase;
  private final String masterKey;
  private final VAPIDFactory factory;
  private final SimpleExecutor executor;
  private final WebClientBase webClientBase;
  private final WebPushRequestFactory128 webPushFactory128;
  private final ConcurrentHashMap<String, FirebaseCachePayload> firebaseCache;

  private class FirebaseCachePayload {
    private final String productId;
    private final GoogleCredentials credentials;
    private final long created;

    public FirebaseCachePayload(String productId, GoogleCredentials credentials) {
      this.productId = productId;
      this.credentials = credentials;
      this.created = System.currentTimeMillis();
    }

    public long age() {
      return System.currentTimeMillis() - created;
    }
  }

  public GlobalPusher(FirstPartyMetrics metrics, DataBase dataBase, String masterKey, SimpleExecutor executor, String email, WebClientBase webClientBase) throws Exception {
    this.metrics = metrics;
    this.dataBase = dataBase;
    this.masterKey = masterKey;
    SecureRandom random = new SecureRandom();
    this.factory = new VAPIDFactory(random);
    this.executor = executor;
    this.webClientBase = webClientBase;
    this.webPushFactory128 = new WebPushRequestFactory128(email, random);
    this.firebaseCache = new ConcurrentHashMap<>();
  }

  private <T> Callback<T> pushFailure(int subscriptionId) {
    return new Callback<T>() {
      @Override
      public void success(T value) {
      }

      @Override
      public void failure(ErrorCodeException ex) {
        // track some metrics
        if (ex.code == ErrorCodes.WEB_CALLBACK_RESOURCE_GONE || ex.code == ErrorCodes.WEB_CALLBACK_RESOURCE_NOT_FOUND) {
          executor.execute(new NamedRunnable("delete-sub") {
            @Override
            public void execute() throws Exception {
              PushSubscriptions.deleteSubscription(dataBase, subscriptionId);
            }
          });
        } else {
          LOGGER.error("push-failure:" + ex.code + ";sub-id=" + subscriptionId);
        }
      }
    };
  }

  private void webPush(ObjectNode rawSub, DeviceSubscription subscription, VAPIDPublicPrivateKeyPair pair, String payload) throws Exception {
    Subscription sub = new Subscription(rawSub);
    SimpleHttpRequest request = webPushFactory128.make(pair, sub, 14, payload.getBytes(StandardCharsets.UTF_8));
    webClientBase.executeShared(request, new VoidCallbackHttpResponder(LOGGER, metrics.webpush_send.start(), pushFailure(subscription.id)));
  }

  private void capacitorPush(int subId, String registrationToken, ObjectNode payload, FirebaseCachePayload cache) {
    ObjectNode root = Json.newJsonObject();
    ObjectNode message = root.putObject("message");
    ObjectNode notif = message.putObject("notification");
    if (payload.has("title")) {
      notif.put("title", payload.get("title").textValue());
    }
    if (payload.has("body")) {
      notif.put("body", payload.get("body").textValue());
    }
    ObjectNode data = message.putObject("data");
    if (payload.has("url")) {
      data.put("url", payload.get("url").textValue());
    }
    if (payload.has("domain")) {
      data.put("domain", payload.get("domain").textValue());
    }
    message.put("token", registrationToken);
    ObjectNode android = message.putObject("android");
    ObjectNode androidNotif = android.putObject("notification");
    if (payload.has("icon")) {
      androidNotif.put("icon", payload.get("icon").textValue());
    }
    try {
      cache.credentials.refreshIfExpired();
      String authToken = cache.credentials.getAccessToken().getTokenValue();
      String postUrl = "https://fcm.googleapis.com/v1/projects/" + cache.productId + "/messages:send";
      TreeMap<String, String> headers = new TreeMap<>();
      headers.put("Authorization", "Bearer " + authToken);
      headers.put("Content-Type", "application/json; UTF-8");
      SimpleHttpRequest request = new SimpleHttpRequest("POST", postUrl, headers, SimpleHttpRequestBody.WRAP(root.toString().getBytes(StandardCharsets.UTF_8)));
      webClientBase.executeShared(request, new StringCallbackHttpResponder(LOGGER, metrics.capacitor_send.start(), pushFailure(subId)));
    } catch (Exception ex) {
      LOGGER.error("send-firebase-notif", ex);
    }
  }

  @Override
  public void notify(String pushTrackingToken, String domain, NtPrincipal who, String payload, Callback<Void> callback) {
    executor.execute(new NamedRunnable("notify") {
      @Override
      public void execute() throws Exception {
        try {
          VAPIDPublicPrivateKeyPair pair = Domains.getOrCreateVapidKeyPair(dataBase, domain, factory); // TODO: cache under a 10 minute timer
          ObjectNode payloadNode = Json.parseJsonObject(payload);
          FirebaseCachePayload firebaseCachePayload = firebaseCache.get(domain);
          if (firebaseCachePayload != null && firebaseCachePayload.age() > 5 * 60000) { // TODO: capture as a magic number
            firebaseCachePayload = null;
          }
          if (firebaseCachePayload == null) {
            try {
              ObjectNode productConfig = Json.parseJsonObject(MasterKey.decrypt(masterKey, Domains.getNativeAppConfig(dataBase, domain)));
              if (productConfig != null) {
                JsonNode firebaseNode = productConfig.get("firebase");
                GoogleCredentials credentials = GoogleCredentials //
                    .fromStream(new ByteArrayInputStream(firebaseNode.toString().getBytes(StandardCharsets.UTF_8))) //
                    .createScoped(Arrays.asList(new String[]{"https://www.googleapis.com/auth/firebase.messaging"}));
                credentials.refresh();
                firebaseCachePayload = new FirebaseCachePayload(firebaseNode.get("project_id").textValue(), credentials);
                firebaseCache.put(domain, firebaseCachePayload);
              }
            } catch (Exception ex) {
              if ((ex instanceof ErrorCodeException)) {
                if (((ErrorCodeException) ex).code != ErrorCodes.CONFIG_NOT_FOUND_FOR_DOMAIN) {
                  LOGGER.error("create-firebase-cache: " + ((ErrorCodeException) ex).code);
                }
              } else {
                LOGGER.error("unknown-create-firebase-cache", ex);
              }
            }
          }

          var subs = PushSubscriptions.list(dataBase, domain, who);
          for (DeviceSubscription subscription : subs) {
            ObjectNode raw = Json.parseJsonObject(subscription.subscription);
            String method = raw.get("@method").textValue();
            if ("webpush".equals(method)) {
              webPush(raw, subscription, pair, payload);
            } else if ("capacitor".equals(method)) { // use firebase stuff
              if (firebaseCachePayload != null) {
                String registrationToken = raw.get("token").textValue();
                capacitorPush(subscription.id, registrationToken, payloadNode, firebaseCachePayload);
              } else {
                LOGGER.error("no-product-config:" + domain);
              }
            }
          }
          callback.success(null);
        } catch (Exception ex) {
          LOGGER.error("failed-notify", ex);
          callback.failure(new ErrorCodeException(ErrorCodes.GLOBAL_PUSHER_UNKNOWN_FAILURE));
        }
      }
    });

  }

  public void install() {
    ServiceRegistry.add("push", Push.class, (space, configRaw, keys) -> new Push(metrics, GlobalPusher.this));
  }
}
