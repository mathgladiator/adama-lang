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

  public RxInvalidate init(Caller caller, String name, SimpleService service, String method, Supplier<NtToDynamic> supplier) {
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
