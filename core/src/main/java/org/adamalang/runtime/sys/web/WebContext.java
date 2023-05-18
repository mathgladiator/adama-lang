/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.sys.web;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;

/** the context (who, from:origin+ip) for a web request */
public class WebContext {
  public final NtPrincipal who;
  public final String origin;
  public final String ip;

  public WebContext(NtPrincipal who, String origin, String ip) {
    this.who = who;
    this.origin = origin;
    this.ip = ip;
  }

  public static WebContext readFromObject(JsonStreamReader reader) {
    if (reader.startObject()) {
      NtPrincipal _who = null;
      String _origin = null;
      String _ip = null;
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "who":
            _who = reader.readNtPrincipal();
            break;
          case "origin":
            _origin = reader.readString();
            break;
          case "ip":
            _ip = reader.readString();
            break;
          default:
            reader.skipValue();
        }
      }
      return new WebContext(_who, _origin, _ip);
    } else {
      reader.skipValue();
    }
    return null;
  }

  public void writeAsObject(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("who");
    writer.writeNtPrincipal(who);
    writer.writeObjectFieldIntro("origin");
    writer.writeString(origin);
    writer.writeObjectFieldIntro("ip");
    writer.writeString(ip);
    writer.endObject();
  }
}
