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
package org.adamalang.runtime.sys.metering;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

import java.util.HashMap;
import java.util.Map;

public class MeterReducerReader {
  public static Map<String, String> convertMapToBillingMessages(String batch, String region, String machine) {
    HashMap<String, String> messages = new HashMap<>();

    HashMap<String, String> records = new HashMap<>();
    long time = 0;
    JsonStreamReader reader = new JsonStreamReader(batch);
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "time":
            time = reader.readLong();
            break;
          case "spaces":
            if (reader.startObject()) {
              while (reader.notEndOfObject()) {
                String space = reader.fieldName();
                records.put(space, reader.skipValueIntoJson());
              }
            } else {
              reader.skipValue();
            }
            break;
          default:
            reader.skipValue();
        }
      }
    }
    for (Map.Entry<String, String> entry : records.entrySet()) {
      JsonStreamWriter message = new JsonStreamWriter();
      message.beginObject();
      message.writeObjectFieldIntro("timestamp");
      message.writeLong(time);
      message.writeObjectFieldIntro("space");
      message.writeString(entry.getKey());
      message.writeObjectFieldIntro("region");
      message.writeString(region);
      message.writeObjectFieldIntro("machine");
      message.writeString(machine);
      message.writeObjectFieldIntro("record");
      message.injectJson(entry.getValue());
      message.endObject();
      messages.put(entry.getKey(), message.toString());
    }
    return messages;
  }
}
