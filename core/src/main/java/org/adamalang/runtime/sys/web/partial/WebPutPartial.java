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
