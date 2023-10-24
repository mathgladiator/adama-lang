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
package org.adamalang.web.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.natives.NtAsset;

import java.util.TreeMap;

/** a simple http handler */
public interface HttpHandler {
  HttpHandler NULL = new HttpHandler() {

    @Override
    public void handleOptions(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
      callback.success(null);
    }

    @Override
    public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
      callback.success(null);
    }

    @Override
    public void handleDelete(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
      callback.success(null);
    }

    @Override
    public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
      callback.success(null);
    }

    @Override
    public void handleDeepHealth(Callback<String> callback) {
      callback.success("NULL");
    }
  };

  void handleOptions(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback);

  void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback);

  void handleDelete(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback);

  void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback);

  void handleDeepHealth(Callback<String> callback);

  /** The concrete result of handling a request; */
  class HttpResult {
    public final String contentType;
    public final byte[] body;
    public final String space;
    public final String key;
    public final NtAsset asset;
    public final boolean cors;
    public final boolean redirect;
    public final String location;
    public final int redirectStatus;
    public final Integer cacheTimeSeconds;

    public HttpResult(String contentType, byte[] body, boolean cors) {
      this.contentType = contentType;
      this.body = body;
      this.space = null;
      this.key = null;
      this.asset = null;
      this.cors = cors;
      this.redirect = false;
      this.location = null;
      this.redirectStatus = 0;
      this.cacheTimeSeconds = null;
    }

    public HttpResult(String space, String key, NtAsset asset, boolean cors, int cts) {
      this.contentType = asset.contentType;
      this.body = null;
      this.space = space;
      this.key = key;
      this.asset = asset;
      this.cors = cors;
      this.redirect = false;
      this.location = null;
      this.redirectStatus = 0;
      this.cacheTimeSeconds = cts > 0 ? cts : null;
    }

    public HttpResult(String location, int code) {
      this.contentType = null;
      this.body = null;
      this.space = null;
      this.key = null;
      this.asset = null;
      this.cors = true;
      this.redirect = true;
      this.location = location;
      this.redirectStatus = code;
      this.cacheTimeSeconds = null;
    }
  }
}
