/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.async;

import org.adamalang.runtime.contracts.AsyncAction;
import org.adamalang.runtime.exceptions.AbortMessageException;
import org.adamalang.runtime.exceptions.RetryProgressException;
import org.adamalang.runtime.natives.NtClient;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** a task is a wrapper around a message, it is used to track the lifecycle of
 * the message and delay executing code from the living document. */
public class AsyncTask {
  private boolean aborted;
  private AsyncAction action;
  public final String channel;
  public final ObjectNode message;
  public final int messageId;
  public final NtClient who;

  /** Construct the task around a message */
  public AsyncTask(final int messageId, final NtClient who, final String channel, final ObjectNode message) {
    this.messageId = messageId;
    this.who = who;
    this.channel = channel;
    this.message = message;
    action = null;
    aborted = false;
  }

  /** execute the task */
  public void execute() throws RetryProgressException {
    /** we must have either an action and not be aborted */
    if (action != null && !aborted) {
      try {
        action.execute(); // compute
      } catch (final AbortMessageException aborted) {
        // this did not go so well
        this.aborted = true;
        throw new RetryProgressException(messageId);
      }
    }
  }

  /** associate code to run on this task. This is done within the generated code
   * to invert the execution flow. */
  public void setAction(final AsyncAction action) {
    this.action = action;
  }
}
