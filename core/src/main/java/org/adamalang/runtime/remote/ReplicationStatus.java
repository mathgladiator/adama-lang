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
    result = new NtResult<>(null, false, 0, "");
    this.hasData = false;
    this.hash = "";
  }

  public boolean needSend() {
    return !hasData || result.failed() || !result.has();
  }

  public boolean desire(String newHash) {
    if (!hash.equals(newHash)) {
      result = new NtResult<>(null, false, 0, "");
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
      boolean failed = false;
      String value = null;
      int failureCode = 0;
      String failureMessage = "";
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
            failed = reader.readBoolean();
            break;
          case "reason":
            failureCode = reader.readInteger();
            break;
          case "reason_message":
            failureMessage = reader.readString();
            break;
          case "value":
            value = reader.readString();
            break;
          default:
            reader.skipValue();
        }
      }
      result = new NtResult<>(value, failed, failureCode, failureMessage);
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
    if (result.has()) {
      writer.writeObjectFieldIntro("value");
      writer.writeString(result.get());
    } else {
      writer.writeObjectFieldIntro("reason");
      writer.writeInteger(result.code());
      writer.writeObjectFieldIntro("reason_message");
      writer.writeString(result.message());
    }
    writer.endObject();
  }
}
