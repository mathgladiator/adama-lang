/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMap;

import java.util.Map;
import java.util.TreeMap;

/** a @web put $path (... ) invocation */
public class WebPut implements WebItem {
  public final WebContext context;
  public final String uri;
  public final NtMap<String, String> headers;
  public final NtDynamic parameters;
  public String bodyJson;

  public WebPut(WebContext context, String uri, TreeMap<String, String> headers, NtDynamic parameters, String bodyJson) {
    this.context = context;
    this.headers = new NtMap<>();
    this.headers.storage.putAll(headers);
    this.uri = uri;
    this.parameters = parameters;
    this.bodyJson = bodyJson;
  }

  @Override
  public void writeAsObject(JsonStreamWriter writer) {
    writer.beginObject();
    injectWrite(writer);
    writer.endObject();
  }

  public void injectWrite(JsonStreamWriter writer) {
    writer.writeObjectFieldIntro("put");
    writer.beginObject();
    writer.writeObjectFieldIntro("uri");
    writer.writeString(uri);
    writer.writeObjectFieldIntro("headers");
    writer.beginObject();
    for (Map.Entry<String, String> entry : headers.entries()) {
      writer.writeObjectFieldIntro(entry.getKey());
      writer.writeString(entry.getValue());
    }
    writer.endObject();
    writer.writeObjectFieldIntro("parameters");
    writer.writeNtDynamic(parameters);
    writer.writeObjectFieldIntro("bodyJson");
    writer.writeNtDynamic(new NtDynamic(bodyJson));
    writer.endObject();
  }

  public JsonStreamReader body() {
    return new JsonStreamReader(bodyJson);
  }
}
