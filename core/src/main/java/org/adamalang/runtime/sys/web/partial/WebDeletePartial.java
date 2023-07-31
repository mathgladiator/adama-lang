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
import org.adamalang.runtime.sys.web.WebDelete;
import org.adamalang.runtime.sys.web.WebItem;

import java.util.TreeMap;

public class WebDeletePartial implements WebPartial {
  public final String uri;
  public final TreeMap<String, String> headers;
  public final NtDynamic parameters;

  public WebDeletePartial(String uri, TreeMap<String, String> headers, NtDynamic parameters) {
    this.uri = uri;
    this.headers = headers;
    this.parameters = parameters;
  }

  @Override
  public WebItem convert(WebContext context) {
    if (uri != null && headers != null && parameters != null) {
      return new WebDelete(context, uri, headers, parameters);
    }
    return null;
  }

  public static WebDeletePartial read(JsonStreamReader reader) {
    String uri = null;
    NtDynamic parameters = null;
    TreeMap<String, String> headers = new TreeMap<>();

    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        final var fieldName = reader.fieldName();
        switch (fieldName) {
          case "uri":
            uri = reader.readString();
            break;
          case "headers":
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
        }
      }
    }

    return new WebDeletePartial(uri, headers, parameters);
  }

}
