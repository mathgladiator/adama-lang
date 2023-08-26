/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
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
