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
