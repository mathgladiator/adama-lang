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
package org.adamalang.runtime.async;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

/** represents a serialized timeout that has been persisted in the document to track timeouts of actions */
public class Timeout {
  public final long timestamp;
  public final double timeoutSeconds;

  public Timeout(long timestamp, double timeoutSeconds) {
    this.timestamp = timestamp;
    this.timeoutSeconds = timeoutSeconds;
  }

  /** read a timeout object */
  public static Timeout readFrom(JsonStreamReader reader) {
    if (reader.startObject()) {
      long timestamp = 0L;
      double timeoutSeconds = 0.0;
      while (reader.notEndOfObject()) {
        final var f = reader.fieldName();
        switch (f) {
          case "timestamp":
            timestamp = reader.readLong();
            break;
          case "timeout":
            timeoutSeconds = reader.readDouble();
            break;
          default:
            reader.skipValue();
        }
      }
      return new Timeout(timestamp, timeoutSeconds);
    } else {
      reader.skipValue();
    }
    return null;
  }

  /** write out a timeout object */
  public void write(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(timestamp);
    writer.writeObjectFieldIntro("timeout");
    writer.writeDouble(timeoutSeconds);
    writer.endObject();
  }
}
