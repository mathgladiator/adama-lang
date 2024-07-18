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
import org.adamalang.common.SimpleExecutor;
import org.adamalang.runtime.contracts.DeleteTask;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxLazy;
import org.adamalang.runtime.sys.LivingDocument;

import java.util.*;

/** engine to replicate data */
public class ReplicationEngine implements DeleteTask  {
  private final LivingDocument document;
  private final ArrayList<RxReplicationStatus> tasks;
  private final HashMap<TombStone, RxReplicationStatus> deleting;

  public ReplicationEngine(LivingDocument parent) {
    this.document = parent;
    this.tasks = new ArrayList<>();
    this.deleting = new HashMap<>();
  }

  public void link(RxReplicationStatus status, RxLazy<? extends NtToDynamic> value) {
    tasks.add(status);
    value.__subscribe(status);
    status.linkToValue(value);
  }

  public void load(JsonStreamReader reader, RxInt64 documentTime) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        String name = reader.fieldName();
        if (reader.testLackOfNull()) {
          TombStone ts = TombStone.read(reader);
          RxReplicationStatus status = new RxReplicationStatus(RxParent.DEAD, documentTime, ts.service, ts.method);
          status.linkToTombstone(ts, document);
          deleting.put(ts, status);
        }
      }
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    for (Map.Entry<TombStone, RxReplicationStatus> entry : deleting.entrySet()) {
      writer.writeObjectFieldIntro(entry.getKey().md5);
      entry.getKey().dump(writer);
    }
    writer.endObject();
  }

  public void commit(JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    ArrayList<TombStone> toAdd = new ArrayList<>();
    ArrayList<String> toDelete = new ArrayList<>();

    { // make progress on items being deleted
      Iterator<Map.Entry<TombStone, RxReplicationStatus>> itDeleting = deleting.entrySet().iterator();
      while (itDeleting.hasNext()) {
        Map.Entry<TombStone, RxReplicationStatus> pair = itDeleting.next();
        pair.getValue().progress(document);
        if (pair.getValue().isGone()) {
          toDelete.add(pair.getKey().md5);
          itDeleting.remove();
        }
      }
    }

    { // make progress on items that are live
      Iterator<RxReplicationStatus> it = tasks.iterator();
      while (it.hasNext()) {
        RxReplicationStatus status = it.next();
        status.progress(document);
        if (status.requiresTombstone()) {
          it.remove();
          TombStone ts = status.toTombStone();
          if (ts != null) {
            deleting.put(ts, status);
            toAdd.add(ts);
          }
        }
      }
    }

    if (toAdd.size() > 0 || toDelete.size() > 0) {
      // there is no reverse/undo for replication
      reverseDelta.writeObjectFieldIntro("__replication");
      reverseDelta.beginObject();
      reverseDelta.endObject();

      forwardDelta.writeObjectFieldIntro("__replication");
      forwardDelta.beginObject();
      for (String del : toDelete) { // remove the tombstones for successful deletions
        forwardDelta.writeObjectFieldIntro(del);
        forwardDelta.writeNull();
      }
      for (TombStone ts : toAdd) { // persist the tombstone for things being deleted
        forwardDelta.writeObjectFieldIntro(ts.md5);
        ts.dump(forwardDelta);
      }
      forwardDelta.endObject();
    }
  }

  public void signalDurableAndExecute(SimpleExecutor executor) {
    for (RxReplicationStatus status : deleting.values()) {
      status.signalDurableAndExecute(executor);
    }
    for (RxReplicationStatus status : tasks) {
      status.signalDurableAndExecute(executor);
    }
  }

  @Override
  public void executeAfterMark(Callback<Void> callback) {
    // This is very tricky! We just marked the document as deleted, so now we need to execute all the things.
    // TODO: execute deletes
    callback.success(null);
  }
}
