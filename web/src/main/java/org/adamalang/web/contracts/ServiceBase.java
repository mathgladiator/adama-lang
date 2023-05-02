/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's Apache2); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.contracts;

import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.assets.AssetSystem;
import org.adamalang.web.io.ConnectionContext;
import org.adamalang.web.io.JsonRequest;
import org.adamalang.web.io.JsonResponder;

/**
 * this is the base of the service which is used to spawn ServiceConnection's when a new request
 * comes online
 */
public interface ServiceBase {

  static ServiceBase JUST_HTTP(HttpHandler http) {
    return new ServiceBase() {
      @Override
      public ServiceConnection establish(ConnectionContext context) {
        return new ServiceConnection() {
          @Override
          public void execute(JsonRequest request, JsonResponder responder) {
            responder.error(new ErrorCodeException(-1));
          }

          @Override
          public boolean keepalive() {
            return false;
          }

          @Override
          public void kill() {
          }
        };
      }

      @Override
      public HttpHandler http() {
        return http;
      }

      @Override
      public AssetSystem assets() {
        return null;
      }
    };
  }

  /** a new connection has presented itself */
  ServiceConnection establish(ConnectionContext context);

  HttpHandler http();

  AssetSystem assets();
}
