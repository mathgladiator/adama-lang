/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.natives.NtAsset;

import java.util.TreeMap;

public interface HttpHandler {
  HttpHandler NULL = new HttpHandler() {
    @Override
    public void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback) {
      callback.success(null);
    }

    @Override
    public void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback) {
      callback.success(null);
    }
  };

  void handleGet(String uri, TreeMap<String, String> headers, String parametersJson, Callback<HttpResult> callback);

  void handlePost(String uri, TreeMap<String, String> headers, String parametersJson, String body, Callback<HttpResult> callback);

  class HttpResult {
    public final String contentType;
    public final byte[] body;
    public final String space;
    public final String key;
    public final NtAsset asset;

    public HttpResult(String contentType, byte[] body) {
      this.contentType = contentType;
      this.body = body;
      this.space = null;
      this.key = null;
      this.asset = null;
    }

    public HttpResult(String space, String key, NtAsset asset) {
      this.contentType = asset.contentType;
      this.body = null;
      this.space = space;
      this.key = key;
      this.asset = asset;
    }
  }
}
