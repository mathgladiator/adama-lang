/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.runtime.natives.NtMessageBase;

public class WebResponse {
  public String bodyContentType;
  public String body;
  public NtAsset asset;

  public WebResponse html(String body) {
    this.bodyContentType = "text/html; charset=utf-8";
    this.body = body;
    return this;
  }

  public WebResponse json(NtMessageBase message) {
    this.bodyContentType = "application/json";
    JsonStreamWriter writer = new JsonStreamWriter();
    message.__writeOut(writer);
    this.body = writer.toString();
    return this;
  }

  public WebResponse asset(NtAsset asset) {
    this.bodyContentType = asset.contentType;
    this.asset = asset;
    return this;
  }
}
