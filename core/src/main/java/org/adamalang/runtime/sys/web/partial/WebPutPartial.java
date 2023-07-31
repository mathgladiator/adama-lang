/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.sys.web.partial;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebItem;
import org.adamalang.runtime.sys.web.WebPut;

import java.util.TreeMap;

public class WebPutPartial implements WebPartial {
  public final String uri;
  public final TreeMap<String, String> headers;
  public final NtDynamic parameters;
  public String bodyJson;

  public WebPutPartial(String uri, TreeMap<String, String> headers, NtDynamic parameters, String bodyJson) {
    this.headers = headers;
    this.uri = uri;
    this.parameters = parameters;
    this.bodyJson = bodyJson;
  }

  public static WebPutPartial read(JsonStreamReader reader) {
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
            if (reader.startObject()) {
              headers = new TreeMap<>();
              while (reader.notEndOfObject()) {
                String key = reader.fieldName();
                headers.put(key, reader.readString());
              }
            } else {
              reader.skipValue();
            }
            break;
          case "parameters":
            parameters = reader.readNtDynamic();
            break;
          case "bodyJson":
            bodyJson = reader.skipValueIntoJson();
            break;
          default:
            reader.skipValue();
        }
      }
    }
    return new WebPutPartial(uri, headers, parameters, bodyJson);
  }

  @Override
  public WebItem convert(WebContext context) {
    if (uri != null && headers != null && parameters != null && bodyJson != null) {
      return new WebPut(context, uri, headers, parameters, bodyJson);
    }
    return null;
  }
}
