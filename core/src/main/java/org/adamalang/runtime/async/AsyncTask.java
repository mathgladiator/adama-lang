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

import org.adamalang.runtime.contracts.AsyncAction;
import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.exceptions.RetryProgressException;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtMessageBase;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.sys.CoreRequestContext;

/**
 * a task is a wrapper around a message, it is used to track the lifecycle of the message and delay
 * executing code from the living document.
 */
public class AsyncTask {
  public final String channel;
  public final Integer viewId; // note: we don't persist this as it is ephemeral
  public final Object message;
  public final int messageId;
  public final long timestamp;
  public final NtPrincipal who;
  public final String origin;
  public final String ip;
  private boolean aborted;
  private AsyncAction action;

  /** Construct the task around a message */
  public AsyncTask(final int messageId, final NtPrincipal who, final Integer viewId, final String channel, final long timestamp, final String origin, String ip, final Object message) {
    this.messageId = messageId;
    this.who = who;
    this.viewId = viewId;
    this.channel = channel;
    this.timestamp = timestamp;
    this.origin = origin;
    this.ip = ip;
    this.message = message;
    action = null;
    aborted = false;
  }

  public CoreRequestContext context(String key) {
    return new CoreRequestContext(who, origin, ip, key);
  }

  /** dump to a Json Stream Writer */
  public void dump(final JsonStreamWriter writer) {
    writer.beginObject();
    writer.writeObjectFieldIntro("who");
    writer.writeNtPrincipal(who);
    writer.writeObjectFieldIntro("channel");
    writer.writeFastString(channel);
    writer.writeObjectFieldIntro("timestamp");
    writer.writeLong(timestamp);
    writer.writeObjectFieldIntro("origin");
    writer.writeFastString(origin);
    writer.writeObjectFieldIntro("ip");
    writer.writeFastString(ip);
    writer.writeObjectFieldIntro("message");
    if (message instanceof NtMessageBase) {
      ((NtMessageBase) message).__writeOut(writer);
    } else if (message instanceof NtMessageBase[]) {
      final var msgs = (NtMessageBase[]) message;
      writer.beginArray();
      for (final NtMessageBase msg : msgs) {
        msg.__writeOut(writer);
      }
      writer.endArray();
    }
    writer.endObject();
  }

  /** execute the task */
  public void execute() throws RetryProgressException {
    // we must have either an action and not be aborted
    if (action != null && !aborted) {
      try {
        action.execute(); // compute
      } catch (final AbortMessageException aborted) {
        // this did not go so well
        this.aborted = true;
        throw new RetryProgressException(this);
      }
    }
  }

  /**
   * associate code to run on this task. This is done within the generated code to invert the
   * execution flow.
   */
  public void setAction(final AsyncAction action) {
    this.action = action;
  }
}
