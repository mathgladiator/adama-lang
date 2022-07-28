/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.runtime.async;

import org.adamalang.runtime.contracts.AsyncAction;
import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.exceptions.RetryProgressException;
import org.adamalang.runtime.json.JsonStreamWriter;
import org.adamalang.runtime.natives.NtPrincipal;
import org.adamalang.runtime.natives.NtMessageBase;

/**
 * a task is a wrapper around a message, it is used to track the lifecycle of the message and delay
 * executing code from the living document.
 */
public class AsyncTask {
  public final String channel;
  public final Object message;
  public final int messageId;
  public final long timestamp;
  public final NtPrincipal who;
  private boolean aborted;
  private AsyncAction action;

  /** Construct the task around a message */
  public AsyncTask(final int messageId, final NtPrincipal who, final String channel, final long timestamp, final Object message) {
    this.messageId = messageId;
    this.who = who;
    this.channel = channel;
    this.timestamp = timestamp;
    this.message = message;
    action = null;
    aborted = false;
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
