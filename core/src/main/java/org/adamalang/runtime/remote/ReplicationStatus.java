/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

public class ReplicationStatus {

  private boolean dirty;

  public ReplicationStatus() {
    this.dirty = true;
  }

  public boolean peekDirty() {
    return dirty;
  }

  public boolean getAndClearDirty() {
    boolean prior = dirty;
    dirty = false;
    return prior;
  }

  public void read(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        String field = reader.fieldName();
        reader.skipValue();
      }
    } else {
      reader.skipValue();
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.endObject();
  }
}
