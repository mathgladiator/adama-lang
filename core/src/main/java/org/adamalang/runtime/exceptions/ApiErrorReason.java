/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.runtime.exceptions;

/** the reason an API request failed */
// TODO: think about the ids hard
public enum ApiErrorReason {
  GamespaceNotFound(1), //
  GameFailedToConstruct(2), //
  GameForbadeConnect(3), //
  GameNotFound(4), //
  AlreadyConnected(5), //
  NoChannelSpecified(6), //
  NoGameIdSpecified(7), //
  RareConflictOnDocumentCreate(8), //
  RequestHasNoDataObject(9), //
  RequestHasNoMethod(10), //
  RequestHasNoResource(11), //
  RequestHasNoSessionYet(12), //
  SendMessageFailure(13), //
  InternalIssue(500), //
  ;

  public final int errorCode;

  private ApiErrorReason(final int errorCode) {
    this.errorCode = errorCode;
  }
}
