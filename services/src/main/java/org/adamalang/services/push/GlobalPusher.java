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

import org.adamalang.ErrorCodes;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.NamedRunnable;
import org.adamalang.common.SimpleExecutor;
import org.adamalang.common.keys.VAPIDFactory;
import org.adamalang.common.keys.VAPIDPublicPrivateKeyPair;
import org.adamalang.mysql.DataBase;
import org.adamalang.mysql.data.DeviceSubscription;
import org.adamalang.mysql.model.Domains;
import org.adamalang.mysql.model.PushSubscriptions;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.ServiceRegistry;
import org.adamalang.services.FirstPartyMetrics;
import org.adamalang.services.ServiceConfig;
import org.adamalang.services.push.webpush.Subscription;
import org.adamalang.services.push.webpush.WebPushRequestFactory128;
import org.adamalang.services.sms.Twilio;
import org.adamalang.web.client.SimpleHttpRequest;
import org.adamalang.web.client.VoidCallbackHttpResponder;
import org.adamalang.web.client.WebClientBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

public class GlobalPusher implements Pusher {
  private final static Logger LOGGER = LoggerFactory.getLogger(GlobalPusher.class);

  private final FirstPartyMetrics metrics;
  private final DataBase dataBase;
  private final VAPIDFactory factory;
  private final SimpleExecutor executor;
  private final WebClientBase webClientBase;
  private final WebPushRequestFactory128 webPushFactory128;

  public GlobalPusher(FirstPartyMetrics metrics, DataBase dataBase, SimpleExecutor executor, String email, WebClientBase webClientBase) throws Exception {
    this.metrics = metrics;
    this.dataBase = dataBase;
    SecureRandom random = new SecureRandom();
    this.factory = new VAPIDFactory(random);
    this.executor = executor;
    this.webClientBase = webClientBase;
    this.webPushFactory128 = new WebPushRequestFactory128(email, random);
  }

  @Override
  public void notify(String domain, NtPrincipal who, String payload, Callback<String> callback) {
    executor.execute(new NamedRunnable("notify") {
      @Override
      public void execute() throws Exception {
        try {
          VAPIDPublicPrivateKeyPair pair = Domains.getOrCreateVapidKeyPair(dataBase, domain, factory);
          var subs = PushSubscriptions.list(dataBase, domain, who);
          callback.success("future-tracking-token");
          for (DeviceSubscription subscription : subs) {
            // TODO: test if the subscription is webpush
            Subscription sub = new org.adamalang.services.push.webpush.Subscription(subscription.subscription);
            SimpleHttpRequest request = webPushFactory128.make(pair, sub, 14, payload.getBytes(StandardCharsets.UTF_8));
            webClientBase.executeShared(request, new VoidCallbackHttpResponder(LOGGER, metrics.webpush_send.start(), new Callback<Void>() {
              int boundId = subscription.id;
              @Override
              public void success(Void value) {
                // track some metrics
              }

              @Override
              public void failure(ErrorCodeException ex) {
                // track some metrics
                if (ex.code == 410) {
                  executor.execute(new NamedRunnable("delete-sub") {
                     @Override
                     public void execute() throws Exception {
                       PushSubscriptions.deleteSubscription(dataBase, boundId);
                     }
                   });
                }
              }
            }));
          }
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
