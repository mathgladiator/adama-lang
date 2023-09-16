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
package org.adamalang.runtime.remote;

import org.adamalang.common.Callback;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.sys.LivingDocument;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ReplicationEngine implements DeleteTask  {
  private final LivingDocument parent;
  private final HashMap<String, ReplicationStateMachine> machines;
  private final HashMap<String, ReplicationStatus> status;

  public ReplicationEngine(LivingDocument parent) {
    this.parent = parent;
    this.machines = new HashMap<>();
    this.status = new HashMap<>();
  }

  protected ReplicationStatus getOrCreateStatus(String name) {
    ReplicationStatus value = status.get(name);
    if (value == null) {
      value = new ReplicationStatus(parent);
      status.put(name, value);
    }
    return value;
  }

  public RxInvalidate init(Caller caller, String name, Service service, String method, Supplier<NtToDynamic> supplier) {
    ReplicationStateMachine sm = new ReplicationStateMachine(caller, name, service, method, supplier, getOrCreateStatus(name));
    machines.put(name, sm);
    return sm.invalidated;
  }

  public void load(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        String name = reader.fieldName();
        if (reader.testLackOfNull()) {
          getOrCreateStatus(name).read(reader);
        }
      }
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    for (Map.Entry<String, ReplicationStatus> entry : status.entrySet()) {
      writer.writeObjectFieldIntro(entry.getKey());
      entry.getValue().dump(writer);
    }
    writer.endObject();
  }

  public void commit(JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    boolean commitDirty = false;
    for (Map.Entry<String, ReplicationStatus> entry : status.entrySet()) {
      if (entry.getValue().peekDirty()) {
        commitDirty = true;
      }
    }
    if (commitDirty) {
      forwardDelta.writeObjectFieldIntro("__replication");
      forwardDelta.beginObject();
      reverseDelta.writeObjectFieldIntro("__replication");
      reverseDelta.beginObject();
      for (Map.Entry<String, ReplicationStatus> entry : status.entrySet()) {
        if (entry.getValue().getAndClearDirty()) {
          forwardDelta.writeObjectFieldIntro(entry.getKey());
          entry.getValue().dump(forwardDelta);
          reverseDelta.writeObjectFieldIntro(entry.getKey());
          reverseDelta.writeNull();
        }
      }
      forwardDelta.endObject();
      reverseDelta.endObject();
    }
  }

  @Override
  public void executeAfterMark(Callback<Void> callback) {
    callback.success(null);
  }
}
