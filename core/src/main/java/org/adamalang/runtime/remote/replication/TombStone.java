/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.runtime.remote.replication;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** represents a future delete */
public class TombStone {
  public final String service;
  public final String method;
  public final String key;

  public TombStone(String service, String method, String key) {
    this.service = service;
    this.method = method;
    this.key = key;
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("s");
    writer.writeString(service);
    writer.writeObjectFieldIntro("m");
    writer.writeString(method);
    writer.writeObjectFieldIntro("k");
    writer.writeString(key);
    writer.endObject();
  }

  public static TombStone read(JsonStreamReader reader) {
    if (reader.startObject()) {
      String service = null;
      String method = null;
      String key = null;
      while (reader.notEndOfObject()) {
        String field = reader.fieldName();
        switch (field) {
          case "s":
          case "service":
            service = reader.readString();
            break;
          case "m":
          case "method":
            method = reader.readString();
            break;
          case "k":
          case "key":
            key = reader.readString();
            break;
          default:
            reader.skipValue();
        }
      }
      if (service != null && method != null && key != null) {
        return new TombStone(service, method, key);
      }
    } else {
      reader.skipValue();
    }
    return null;
  }
}
