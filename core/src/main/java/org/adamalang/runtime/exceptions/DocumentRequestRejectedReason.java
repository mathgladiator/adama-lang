/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

/** why document changes may be rejected */
public enum DocumentRequestRejectedReason {
  AlreadyConstructed(ApiErrorReason.InternalIssue), //
  ClientAlreadyConnected(ApiErrorReason.AlreadyConnected), //
  ClientConnectRejected(ApiErrorReason.GameForbadeConnect), //
  ClientNotAlreadyConnected(ApiErrorReason.InternalIssue), //
  ClientNotConnectedForSend(ApiErrorReason.SendMessageFailure), //
  CommandNotRecognized(ApiErrorReason.InternalIssue), //
  DirectoryNotFound(ApiErrorReason.InternalIssue), //
  GameNotFound(ApiErrorReason.GameNotFound), //
  GamespaceNotFound(ApiErrorReason.GamespaceNotFound), //
  NoConstructorArg(ApiErrorReason.InternalIssue), //
  NoRequestCommand(ApiErrorReason.InternalIssue), //
  NoRequestTimestamp(ApiErrorReason.InternalIssue), //
  NoRequestWho(ApiErrorReason.InternalIssue), //
  SendHasNoChannel(ApiErrorReason.InternalIssue), //
  SendHasNoMessage(ApiErrorReason.InternalIssue), //
  ;

  public final ApiErrorReason publicReason;

  private DocumentRequestRejectedReason(final ApiErrorReason publicReason) {
    this.publicReason = publicReason;
  }
}
