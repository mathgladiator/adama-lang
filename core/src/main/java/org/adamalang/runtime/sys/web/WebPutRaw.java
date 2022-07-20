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

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;

import java.util.Map;
import java.util.TreeMap;

public class WebPutRaw {
  public final String uri;
  public final TreeMap<String, String> headers;
  public final NtDynamic parameters;
  public String bodyJson;

  public WebPutRaw(String uri, TreeMap<String, String> headers, NtDynamic parameters, String bodyJson) {
    this.uri = uri;
    this.headers = headers;
    this.parameters = parameters;
    this.bodyJson = bodyJson;
  }

  public static WebPutRaw read(JsonStreamReader reader) {
    String uri = null;
    NtDynamic parameters = null;
    TreeMap<String, String> headers = null;
    String bodyJson = null;

    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var fieldName = reader.fieldName();
        switch (fieldName) {
          case "uri":
            uri = reader.readString();
            break;
          case "headers":
            headers = new TreeMap<>();
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                String key = reader.fieldName();
                headers.put(key, reader.readString());
              }
            }
            break;
          case "parameters":
            parameters = reader.readNtDynamic();
            break;
          case "bodyJson":
            bodyJson = reader.skipValueIntoJson();
            break;
        }
      }
    }

    if (uri != null && headers != null && parameters != null && bodyJson != null) {
      return new WebPutRaw(uri, headers, parameters, bodyJson);
    }
    return null;
  }

  public void writeBody(JsonStreamWriter writer) {
    writer.writeObjectFieldIntro("put");
    writer.beginObject();
    writer.writeObjectFieldIntro("uri");
    writer.writeString(uri);
    writer.writeObjectFieldIntro("headers");
    writer.beginObject();
    for (Map.Entry<String, String> entry : headers.entrySet()) {
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
}
