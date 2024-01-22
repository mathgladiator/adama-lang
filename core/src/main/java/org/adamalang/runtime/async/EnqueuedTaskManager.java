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
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtPrincipal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

/** the enqueued task manager is for future execution of messages when the system is settled */
public class EnqueuedTaskManager {
  private final LinkedList<EnqueuedTask> active;
  private final ArrayList<EnqueuedTask> pending;
  private final ArrayList<EnqueuedTask> completed;

  public EnqueuedTaskManager() {
    this.active = new LinkedList<>();
    this.pending = new ArrayList<>();
    this.completed = new ArrayList<>();
  }

  public void hydrate(JsonStreamReader reader) {
    HashSet<Integer> present = new HashSet<>();
    for (EnqueuedTask task : active) {
      present.add(task.messageId);
    }
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        int messageId = Integer.parseInt(reader.fieldName());
        String _channel = null;
        NtPrincipal _who = null;
        NtDynamic _message = null;
        if (reader.startObject()) {
          while (reader.notEndOfObject()) {
            switch (reader.fieldName()) {
              case "who":
                _who = reader.readNtPrincipal();
                break;
              case "channel":
                _channel = reader.readString();
                break;
              case "message":
                _message = reader.readNtDynamic();
                break;
              default:
                reader.skipValue();
            }
          }
          if (!present.contains(messageId)) {
            active.add(new EnqueuedTask(messageId, _who, _channel, _message));
          }
        } else {
          reader.skipValue();
        }
      }
    } else {
      reader.skipValue();
    }
  }

  public void commit(JsonStreamWriter forward, JsonStreamWriter reverse) {
    if (pending.size() > 0 || completed.size() > 0) {
      forward.writeObjectFieldIntro("__enqueued");
      forward.beginObject();
      reverse.writeObjectFieldIntro("__enqueued");
      reverse.beginObject();
      for (EnqueuedTask task : pending) {
        forward.writeObjectFieldIntro(task.messageId);
        task.writeTo(forward);
        reverse.writeObjectFieldIntro(task.messageId);
        reverse.writeNull();
        active.add(task);
      }
      for (EnqueuedTask task : completed) {
        forward.writeObjectFieldIntro(task.messageId);
        forward.writeNull();
        reverse.writeObjectFieldIntro(task.messageId);
        task.writeTo(reverse);
      }
      pending.clear();
      completed.clear();
      forward.endObject();
      reverse.endObject();
    }
  }

  public void revert() {
    pending.clear();
  }

  public void dump(JsonStreamWriter writer) {
    if (active.size() > 0) {
      writer.writeObjectFieldIntro("__enqueued");
      writer.beginObject();
      for (EnqueuedTask task : active) {
        writer.writeObjectFieldIntro(task.messageId);
        task.writeTo(writer);
      }
      writer.endObject();
    }
  }

  public void add(EnqueuedTask task) {
    pending.add(task);
  }

  public boolean readyForTransfer() {
    return pending.size() == 0 && !active.isEmpty();
  }

  public EnqueuedTask transfer() {
    EnqueuedTask task = active.removeFirst();
    completed.add(task);
    return task;
  }

  public int size() {
    return pending.size() + active.size();
  }
}
