package org.adamalang.runtime.sys.web.partial;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtMap;
import org.adamalang.runtime.sys.web.WebContext;
import org.adamalang.runtime.sys.web.WebGet;
import org.adamalang.runtime.sys.web.WebItem;

import java.util.TreeMap;

public class WebGetPartial implements WebPartial {
  public final String uri;
  public final TreeMap<String, String> headers;
  public final NtDynamic parameters;

  public WebGetPartial(String uri, TreeMap<String, String> headers, NtDynamic parameters) {
    this.uri = uri;
    this.headers = headers;
    this.parameters = parameters;
  }

  public static WebGetPartial read(JsonStreamReader reader) {
    String uri = null;
    NtDynamic parameters = null;
    TreeMap<String, String> headers = null;

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
            } else {
              reader.skipValue();
            }
            break;
          case "parameters":
            parameters = reader.readNtDynamic();
            break;
          default:
            reader.skipValue();
        }
      }
    }
    return new WebGetPartial(uri, headers, parameters);
  }


  @Override
  public WebItem convert(WebContext context) {
    return new WebGet(context, uri, headers, parameters);
  }
}
