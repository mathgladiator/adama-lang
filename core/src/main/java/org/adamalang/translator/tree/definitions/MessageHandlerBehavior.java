/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.translator.tree.definitions;

/** how to handle a message */
public enum MessageHandlerBehavior {
  EnqueueItemIntoNativeChannel, // the item should be enqueued
  ExecuteAssociatedCode //
}
