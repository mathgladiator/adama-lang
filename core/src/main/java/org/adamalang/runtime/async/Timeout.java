/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
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

  /** write out a timeout object */
  public void write(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(timestamp);
    writer.writeObjectFieldIntro("timeout");
    writer.writeDouble(timeoutSeconds);
    writer.endObject();
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
}
