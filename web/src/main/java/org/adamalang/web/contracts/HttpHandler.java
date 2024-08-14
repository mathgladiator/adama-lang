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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.adamalang.common.Callback;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.io.ConnectionContext;

import java.util.TreeMap;

/** a simple http handler */
public interface HttpHandler {

  public enum Method {
    OPTIONS,
    GET,
    DELETE,
    PUT
  };


  HttpHandler NULL = new HttpHandler() {
    @Override
    public void handle(ConnectionContext context, Method method, String identity, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
      callback.success(null);
    }

    @Override
    public void handleDeepHealth(Callback<String> callback) {
      callback.success("NULL");
    }
  };

  void handle(ConnectionContext context, Method method, String identity, String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback);

  void handleDeepHealth(Callback<String> callback);

  /** The concrete result of handling a request; */
  class HttpResult {
    public final int status;
    public final String contentType;
    public final byte[] body;
    public final String space;
    public final String key;
    public final NtAsset asset;
    public final String transform;
    public final boolean cors;
    public final boolean redirect;
    public final String location;
    public final Integer cacheTimeSeconds;
    public final TreeMap<String, String> headers;

    public HttpResult(int status, String contentType, byte[] body, boolean cors) {
      this.status = status;
      this.contentType = contentType != null ? contentType : "";
      this.body = body;
      this.space = null;
      this.key = null;
      this.asset = null;
      this.transform = null;
      this.cors = cors;
      this.redirect = false;
      this.location = null;
      this.cacheTimeSeconds = null;
      this.headers = null;
    }

    public HttpResult(int status, String contentType, byte[] body, boolean cors, TreeMap<String, String> headers) {
      this.status = status;
      this.contentType = contentType != null ? contentType : "";
      this.body = body;
      this.space = null;
      this.key = null;
      this.asset = null;
      this.transform = null;
      this.cors = cors;
      this.redirect = false;
      this.location = null;
      this.cacheTimeSeconds = null;
      this.headers = headers;
    }

    public HttpResult(int status, String space, String key, NtAsset asset, String transform, boolean cors, int cts) {
      this.status = status;
      this.contentType = asset.contentType;
      this.body = null;
      this.space = space;
      this.key = key;
      this.asset = asset;
      this.transform = transform;
      this.cors = cors;
      this.redirect = false;
      this.location = null;
      this.cacheTimeSeconds = cts > 0 ? cts : null;
      this.headers = null;
    }

    public HttpResult(String location, int code) {
      this.status = code;
      this.contentType = null;
      this.body = null;
      this.space = null;
      this.key = null;
      this.asset = null;
      this.transform = null;
      this.cors = true;
      this.redirect = true;
      this.location = location;
      this.cacheTimeSeconds = null;
      this.headers = null;
    }

    public void logInto(ObjectNode logItem) {
      if (asset != null) {
        logItem.put("asset", asset.id);
      }
      if (contentType != null) {
        logItem.put("content-type", contentType);
      }
    }
  }
}
