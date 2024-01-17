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
package org.adamalang.overlord.html;

import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.web.contracts.HttpHandler;
import org.adamalang.web.io.ConnectionContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/** a very dumb handler with no logic; just show what is in the map */
public class ConcurrentCachedHttpHandler implements HttpHandler {
  private final ConcurrentHashMap<String, HttpResult> uris;

  public ConcurrentCachedHttpHandler() {
    this.uris = new ConcurrentHashMap<>();
  }

  @Override
  public void handle(ConnectionContext context, Method method, String identity, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
    if (method == Method.GET) {
      callback.success(uris.get(uri));
    } else {
      callback.success(new HttpResult(200, "", new byte[0], false));
    }
  }

  public void put(String uri, String html) {
    uris.put(uri, new HttpResult(200, "text/html; charset=UTF-8", html.getBytes(StandardCharsets.UTF_8), false));
  }

  public void put(String uri, HttpResult result) {
    uris.put(uri, result);
  }

  @Override
  public void handleDeepHealth(Callback<String> callback) {
    callback.success("OVERLORD");
  }
}
