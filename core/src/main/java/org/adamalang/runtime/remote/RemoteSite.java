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

/** the binding site where inputs are connected to outputs within a differential state machine */
public class RemoteSite {
  private RemoteInvocation invocation;
  private RemoteResult backup;
  private RemoteResult value;
  private Object cached;

  public RemoteSite(RemoteInvocation invocation) {
    this.invocation = invocation;
    this.backup =  RemoteResult.NULL;
    this.value = backup;
    this.cached = null;
  }

  public RemoteSite(JsonStreamReader reader) {
    patch(reader);
    this.backup = this.value;
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
