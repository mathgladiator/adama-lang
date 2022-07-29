/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.sys.PredictiveInventory;

public class WebResponse {
  public String contentType = null;
  public String body = null;
  public NtAsset asset = null;
  public boolean cors = false;
  public int cache_ttl_seconds = 0;
  public String asset_transform = "";

  public WebResponse html(String body) {
    this.contentType = "text/html; charset=utf-8";
    this.body = body;
    return this;
  }

  public WebResponse js(String body) {
    this.contentType = "text/javascript";
    this.body = body;
    return this;
  }

  public WebResponse css(String body) {
    this.contentType = "text/css";
    this.body = body;
    return this;
  }

  public WebResponse cors(boolean cors) {
    this.cors = cors;
    return this;
  }

  public WebResponse cache_ttl_seconds(int cache_ttl_seconds) {
    this.cache_ttl_seconds = cache_ttl_seconds;
    return this;
  }

  public WebResponse asset_transform(String asset_transform) {
    this.asset_transform = asset_transform;
    return this;
  }

  public WebResponse xml(String body) {
    this.contentType = "application/xml";
    this.body = body;
    return this;
  }

  public WebResponse json(NtMessageBase message) {
    this.contentType = "application/json";
    JsonStreamWriter writer = new JsonStreamWriter();
    message.__writeOut(writer);
    this.body = writer.toString();
    return this;
  }

  public WebResponse asset(NtAsset asset) {
    this.contentType = asset.contentType;
    this.asset = asset;
    return this;
  }

  public void account(PredictiveInventory inventory) {
    if (this.body != null) {
      inventory.bandwidth(body.length());
    }
    if (this.asset != null) {
      inventory.bandwidth(this.asset.size);
    }
  }
}
