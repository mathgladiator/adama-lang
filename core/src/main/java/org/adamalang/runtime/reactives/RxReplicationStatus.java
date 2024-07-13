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
package org.adamalang.runtime.reactives;

import org.adamalang.common.Hashing;
import org.adamalang.runtime.contracts.Caller;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtDynamic;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.remote.Service;
import org.adamalang.runtime.remote.replication.Replicator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Used for storing replication status within the document. */
public class RxReplicationStatus extends RxBase implements RxChild {
  // setup state
  private final RxInt64 documentTime;
  private final String service;
  private final String method;
  private RxLazy<? extends NtToDynamic> value;

  // persisted state
  private State state;
  private String key;
  private String hash;
  private long time;

  // transitory local state
  private boolean invalidated;
  private boolean sendOwed;

  // the state machine
  private enum State {
    Fresh(100),
    Sending_Ready(110),
    Sending_Inflight(120),
    Deleting_Inflight(150),
    Replicated(200),
    Failed(500);

    public final int code;

    private State(int code) {
      this.code = code;
    }

    public static State from(int code) {
      for (State state : State.values()) {
        if (state.code == code) {
          return state;
        }
      }
      return Fresh;
    }

    public State inserted() {
      if (this == State.Sending_Inflight) {
        return State.Sending_Ready;
      }
      return this;
    }
  }

  public RxReplicationStatus(RxParent __parent, RxInt64 documentTime, String service, String method) {
    super(__parent);
    this.documentTime = documentTime;
    this.service = service;
    this.method = method;
    this.state = State.Fresh;
    this.hash = null;
    this.key = null;
    this.time = 0;
    this.invalidated = true;
  }

  /** link the status to the value */
  public void linkToValue(RxLazy<? extends NtToDynamic> value) {
    this.value = value;
  }

  @Override
  public void __commit(String name, JsonStreamWriter forwardDelta, JsonStreamWriter reverseDelta) {
    if (__isDirty()) {
      forwardDelta.writeObjectFieldIntro(name);
      __dump(forwardDelta);
      __lowerDirtyCommit();
    }
  }

  @Override
  public void __dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("state");
    writer.writeInteger(state.code);
    if (this.hash != null) {
      writer.writeObjectFieldIntro("hash");
      writer.writeString(hash);
    }
    if (this.key != null) {
      writer.writeObjectFieldIntro("key");
      writer.writeString(key);
    }
    if (this.time > 0) {
      writer.writeObjectFieldIntro("time");
      writer.writeLong(time);
    }
    writer.endObject();
  }

  @Override
  public void __insert(JsonStreamReader reader) {
    if(reader.startObject()) {
      while (reader.notEndOfObject()) {
        String field = reader.fieldName();
        switch (field) {
          case "state":
            this.state = State.from(reader.readInteger()).inserted();
            break;
          case "hash":
            this.hash = reader.readString();
            break;
          case "key":
            this.key = reader.readString();
            break;
          case "time":
            this.time = reader.readLong();
            break;
        }
      }
    } else {
      reader.skipValue();
    }
  }

  @Override
  public void __patch(JsonStreamReader reader) {
    // we don't patch precisely so we
  }

  @Override
  public void __revert() {
  }

  @Override
  public boolean __raiseInvalid() {
    invalidated = true;
    return __parent.__isAlive();
  }

  public void progress(Caller caller) {
    if (invalidated) {
      Service serviceToUse = caller.__findService(service);
      if (serviceToUse == null) {
        state = State.Failed;
        return;
      }
      NtToDynamic valueToSync = value.get();
      Replicator replicator = serviceToUse.beginCreateReplica(method, valueToSync);
      String newKey = replicator.key();
      final String newHash;
      {
        MessageDigest digest = Hashing.md5();
        digest.update(valueToSync.to_dynamic().json.getBytes(StandardCharsets.UTF_8));
        newHash = Hashing.finishAndEncode(digest);
      }



      switch (state) {
        case Sending_Ready:
          if (sendOwed) {
            sendOwed = false;
            send();
            return;
          }
          // TODO: use
          boolean notYetTimedOut = true;
          if (notYetTimedOut) {
            return;
          }
        case Sending_Inflight:
          // ignore
          return;
        case Fresh:
        case Replicated:
        case Failed:
          replicate();
      }
    }
  }

  private void send() {
     // get the service
    // service.method, callback
    // set inflight
    state = State.Sending_Inflight;
    getTime();
  }

  private void replicate() {
    state = State.Sending_Ready;
    sendOwed = true;
    getTime();
  }

  private void getTime() {
    this.time = documentTime.get();
  }

  @Override
  public String toString() {
    return state.toString() + ";" + key + ";" + hash + ";" + time;
  }
}
