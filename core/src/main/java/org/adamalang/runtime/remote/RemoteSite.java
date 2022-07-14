/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.runtime.remote;

import org.adamalang.runtime.json.JsonStreamReader;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtResult;

import java.util.function.Function;

/** the binding site where inputs are connected to outputs within a differential state machine */
public class RemoteSite {
  public final int id;
  private RemoteInvocation invocation;
  private RemoteResult backup;
  private RemoteResult value;
  private Object cached;

  public RemoteSite(int id, RemoteInvocation invocation) {
    this.id = id;
    this.invocation = invocation;
    this.backup =  RemoteResult.NULL;
    this.value = backup;
    this.cached = null;
  }

  public RemoteSite(int id, JsonStreamReader reader) {
    this.id = id;
    patch(reader);
    this.backup = this.value;
  }

  public RemoteInvocation invocation() {
    return this.invocation;
  }

  public <T> NtResult<T> of(Function<String, T> parser) {
    if (cached == null && this.value.result != null) {
      cached = parser.apply(this.value.result);
    }
    if (cached != null) {
      return new NtResult<>((T) cached, false, 0, "OK");
    } else {
      if (this.value.failure != null) {
        return new NtResult<>(null, true, this.value.failureCode, this.value.failure);
      } else {
        return new NtResult<>(null, false, 0, "In progress");
      }
    }
  }

  public void deliver(RemoteResult result) {
    this.value = result;
  }

  public void patch(JsonStreamReader reader) {
    if (reader.startObject()) {
      while (reader.notEndOfObject()) {
        switch (reader.fieldName()) {
          case "invoke":
            invocation = new RemoteInvocation(reader);
            break;
          case "result":
            value = new RemoteResult(reader);
            break;
        }
      }
    }
  }

  public void dump(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("invoke");
    invocation.write(writer);
    writer.writeObjectFieldIntro("result");
    value.write(writer);
    writer.endObject();
  }

  public void writeValue(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("result");
    value.write(writer);
    writer.endObject();
  }

  public void writeBackup(JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("result");
    backup.write(writer);
    writer.endObject();
  }

  public boolean shouldCommit() {
    return value != backup;
  }

  public void commit() {
    backup = value;
  }

  public void revert() {
    value = backup;
  }
}
