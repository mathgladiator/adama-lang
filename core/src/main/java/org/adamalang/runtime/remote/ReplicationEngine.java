/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;

public class ReplicationEngine implements DeleteTask  {

  private boolean dirty;

  public ReplicationEngine() {
    this.dirty = false;
  }

  public void load(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        String name = reader.fieldName();
        reader.skipValue();
      }
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.endObject();
  }

  public void commit(JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (dirty) {
      forwardDelta.writeObjectFieldIntro("__replication");
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro("__replication");
      reverseDelta.beginObject();
      // TODO
      forwardDelta.endObject();
      reverseDelta.endObject();
    }
  }

  @Override
  public void executeAfterMark(Callback<Void> callback) {
    callback.success(null);
  }
}
