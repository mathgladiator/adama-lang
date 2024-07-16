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

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.reactives.RxLazy;
import org.adamalang.runtime.sys.LivingDocument;

import java.util.ArrayList;

/** engine to replicate data */
public class ReplicationEngine implements DeleteTask  {
  private final LivingDocument parent;
  private final ArrayList<RxReplicationStatus> tasks;

  public ReplicationEngine(LivingDocument parent) {
    this.parent = parent;
    this.tasks = new ArrayList<>();
  }

  public void link(RxReplicationStatus status, RxLazy<? extends NtToDynamic> value) {
    tasks.add(status);
    value.__subscribe(status);
    status.linkToValue(value);
  }

  public void progress() {
    for(RxReplicationStatus status : tasks) {
      status.progress(parent);
    }
  }

  public void commitDurable() {
    for(RxReplicationStatus status : tasks) {
      status.commit();
    }
  }

  public void load(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        String name = reader.fieldName();
        if (reader.testLackOfNull()) {
          reader.skipValue();
        }
      }
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.endObject();
  }

  public void commit(JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    boolean commitDirty = false;
    if (commitDirty) {
      forwardDelta.writeObjectFieldIntro("__replication");
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro("__replication");
      reverseDelta.beginObject();

      /*
      for (Map.Entry<String, ReplicationStatus> entry : status.entrySet()) {
        if (entry.getValue().getAndClearDirty()) {
          forwardDelta.writeObjectFieldIntro(entry.getKey());
          entry.getValue().dump(forwardDelta);
          reverseDelta.writeObjectFieldIntro(entry.getKey());
          reverseDelta.writeNull();
        }
      }
      */
      forwardDelta.endObject();
      reverseDelta.endObject();
    }
  }

  @Override
  public void executeAfterMark(Callback<Void> callback) {
    // TODO: execute deletes
    callback.success(null);
  }
}
