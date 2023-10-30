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

import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;

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

  public CoreRequestContext toCoreRequestContext(Key key) {
    return new CoreRequestContext(who, origin, ip, key.key);
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
