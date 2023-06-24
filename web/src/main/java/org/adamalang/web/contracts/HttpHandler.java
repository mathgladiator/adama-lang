/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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

    public HttpResult(String contentType, byte[] body, boolean cors) {
      this.contentType = contentType;
      this.body = body;
      this.space = null;
      this.key = null;
      this.asset = null;
      this.cors = cors;
    }

    public HttpResult(String space, String key, NtAsset asset, boolean cors) {
      this.contentType = asset.contentType;
      this.body = null;
      this.space = space;
      this.key = key;
      this.asset = asset;
      this.cors = cors;
    }
  }
}
