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
