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
import org.adamalang.runtime.natives.NtResult;
import org.adamalang.runtime.sys.LivingDocument;

public class ReplicationStatus {

  private boolean dirty;
  public final RxCache cache;
  private boolean hasData;
  public NtResult<String> result;
  private String hash;

  public ReplicationStatus(LivingDocument parent) {
    this.dirty = true;
    cache = new RxCache(parent, null);
    result = new NtResult<>("", false, 0, "");
    this.hasData = false;
    this.hash = "";
  }

  public boolean needSend() {
    return !hasData || result.failed();
  }

  public boolean desire(String newHash) {
    if (!hash.equals(newHash)) {
      result = new NtResult<>("", false, 0, "");
      dirty = true;
      hash = newHash;
      return true;
    }
    return false;
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
        switch (field) {
          case "cache":
            cache.__insert(reader);
            break;
          case "hash":
            hash = reader.readString();
            break;
          case "failed":
            result = new NtResult<>("", reader.readBoolean(), 0, "");
            break;
          default:
            reader.skipValue();
        }
      }
    } else {
      reader.skipValue();
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("cache");
    cache.__dump(writer);
    writer.writeObjectFieldIntro("hash");
    writer.writeString(hash);
    writer.writeObjectFieldIntro("failed");
    writer.writeBoolean(result.failed());
    writer.endObject();
  }
}
