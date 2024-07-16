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

import org.adamalang.common.*;
import org.adamalang.runtime.contracts.Caller;
import org.adamalang.runtime.contracts.RxChild;
import org.adamalang.runtime.contracts.RxParent;
import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtToDynamic;
import org.adamalang.runtime.reactives.RxBase;
import org.adamalang.runtime.reactives.RxInt64;
import org.adamalang.runtime.reactives.RxLazy;
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
  private int backoff;

  // transitory local state
  private boolean invalidated;
  private boolean executeRequest;

  // goal state (intention cached and computed on change)
  private Service goalServiceToUse;
  private NtToDynamic valueToSync;
  private Replicator replicatorToUse;
  private String newKey = null;
  private String newHash = null;

  // the state machine
  private enum State {
    Nothing(100),
    PutRequested(110),
    PutInflight(120),
    PutFailed(150),
    DeleteRequested(300),
    DeleteInflight(310),
    DeleteFailed(350);

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
      return Nothing;
    }

    public State persist() {
      switch (this) {
        case Nothing:
          return Nothing;
        case PutRequested:
        case PutInflight:
          return PutRequested;
        case DeleteRequested:
        case DeleteInflight:
          return DeleteRequested;
      }
      return this;
    }
  }

  public RxReplicationStatus(RxParent __parent, RxInt64 documentTime, String service, String method) {
    super(__parent);
    this.documentTime = documentTime;
    this.service = service;
    this.method = method;
    this.state = State.Nothing;
    this.hash = null;
    this.key = null;
    this.time = 0;
    this.backoff = 0;
    this.invalidated = true;
    this.executeRequest = false;
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
  public synchronized void __dump(JsonStreamWriter writer) {
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
    if (this.backoff > 0) {
      writer.writeObjectFieldIntro("backoff");
      writer.writeInteger(backoff);
    }
    writer.endObject();
  }

  @Override
  public synchronized void __insert(JsonStreamReader reader) {
    if(reader.startObject()) {
      while (reader.notEndOfObject()) {
        String field = reader.fieldName();
        switch (field) {
          case "state":
            this.state = State.from(reader.readInteger()).persist();
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

  private boolean updateGoal(Caller caller, boolean updateOnlyIfSameKey) {
    NtToDynamic newValue = null;
    String computedKey = null;
    String computedHash = null;
    goalServiceToUse = caller.__findService(service);
    if (goalServiceToUse == null) {
      return false;
    }
    newValue = value.get();
    replicatorToUse = goalServiceToUse.beginCreateReplica(method, newValue);
    if (replicatorToUse == null) {
      return false;
    }
    computedKey = replicatorToUse.key();
    {
      MessageDigest digest = Hashing.md5();
      digest.update(newValue.to_dynamic().json.getBytes(StandardCharsets.UTF_8));
      computedHash = Hashing.finishAndEncode(digest);
    }
    if (updateOnlyIfSameKey) {
      if (this.newKey == null || this.newKey.equals(computedKey)) {
        this.valueToSync = newValue;
        this.newKey = computedKey;
        this.newHash = computedHash;
        return true;
      }
      return false;
    } else {
      this.valueToSync = newValue;
      this.newKey = computedKey;
      this.newHash = computedHash;
      return true;
    }
  }

  public void progress(Caller caller) {
    switch (state) {
      case Nothing: {
        if (invalidated) {
          if (updateGoal(caller, false)) {
            boolean createDirect = key == null && newKey != null;
            boolean overwrite = key != null && key.equals(newKey) && hash != null && !hash.equals(newHash);
            boolean deleteDirect = key != null && newKey == null;
            boolean deleteChange = key != null && !key.equals(newKey);
            if (createDirect || overwrite) {
              state = State.PutRequested;
              key = newKey;
              executeRequest = true;
              time = documentTime.get();
              __raiseDirty();
            } else if (deleteDirect || deleteChange) {
              state = State.DeleteRequested;
              key = newKey;
              executeRequest = true;
              time = documentTime.get();
              __raiseDirty();
            }
          }
          invalidated = false;
        }
        return;
      }
      case PutRequested:
      case DeleteRequested:
        // Cold Restart Condition; TODO: remove magic 60000
        if (time + 60000 > documentTime.get() && !executeRequest) {
          executeRequest = true;
        }
        return;
      case PutFailed:
      case DeleteFailed:
      case PutInflight:
      case DeleteInflight:
        // no-op
        return;
    }
  }

  private int bumpBackoff() {
    backoff ++;
    backoff = (int) (backoff + Math.random() * backoff);
    if (backoff > 30000) {
      backoff = (int) (backoff + Math.random() * backoff);
    }
    if (backoff > 60000 * 60) {
      backoff = 60000 * 60;
    }
    return backoff;
  }

  public void commit(SimpleExecutor executor) {
    executor.execute(new NamedRunnable("rxreplicate-enter") {
      @Override
      public void execute() throws Exception {
        if (executeRequest) {
          executeRequest = false;
          if (state == State.PutRequested) {
            state = State.PutInflight;
            replicatorToUse.complete(new Callback<Void>() {
              @Override
              public void success(Void value) {
                executor.execute(new NamedRunnable("rxreplicate-put-success") {
                  @Override
                  public void execute() throws Exception {
                    // put was successful
                    state = State.Nothing;
                    hash = newHash;
                    time = documentTime.get();
                    __raiseDirty();
                  }
                });
              }

              @Override
              public void failure(ErrorCodeException ex) {
                executor.execute(new NamedRunnable("rxreplicate-put-failure") {
                  @Override
                  public void execute() throws Exception {
                    // put failed, so we retry
                    state = State.PutFailed;
                    time = documentTime.get();
                    int backoffToUse = bumpBackoff();
                    __raiseDirty();
                    executor.schedule(new NamedRunnable("rxreplicate-put-retry") {
                      @Override
                      public void execute() throws Exception {
                        state = State.PutRequested;
                        time = documentTime.get();
                        executeRequest = true;
                        __raiseDirty();
                        commit(executor);
                      }
                    }, backoffToUse);
                  }
                });
              }
            });
          } else if (state == State.DeleteRequested) {
            state = State.DeleteInflight;
            goalServiceToUse.deleteReplica(method, key, new Callback<Void>() {
              @Override
              public void success(Void value) {
                executor.execute(new NamedRunnable("rxreplicate-delete-success") {
                  @Override
                  public void execute() throws Exception {
                    // delete was successful
                    state = State.Nothing;
                    key = null;
                    hash = null;
                    time = 0;
                    backoff = 0;
                    __raiseDirty();
                  }
                });
              }

              @Override
              public void failure(ErrorCodeException ex) {
                executor.execute(new NamedRunnable("rxreplicate-delete-failure") {
                  @Override
                  public void execute() throws Exception {
                    // put failed, so we retry
                    state = State.DeleteFailed;
                    time = documentTime.get();
                    int backoffToUse = bumpBackoff();
                    __raiseDirty();
                    executor.schedule(new NamedRunnable("rxreplicate-delete-retry") {
                      @Override
                      public void execute() throws Exception {
                        state = State.DeleteRequested;
                        time = documentTime.get();
                        executeRequest = true;
                        commit(executor);
                        __raiseDirty();
                      }
                    }, backoffToUse);
                  }
                });
              }
            });
          }
        }
      }
    });
  }

  @Override
  public String toString() {
    return state.toString() + ";" + key + ";" + hash + ";" + time;
  }
}
