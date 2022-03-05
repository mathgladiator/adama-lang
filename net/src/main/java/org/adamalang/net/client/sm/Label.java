/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.sm;

/** the state machine label for where we are during connection establishment */
public enum Label {
  NotConnected, // the state machine is not connected (default state)

  FindingClientWait, // upon finding a target, we search for a client to use

  FindingClientCancelTryNewTarget, // while we were finding the target, a new target to connect too
  // became interesting. We need to abort this and try again

  FindingClientCancelStop, // while we were finding the target, the host disappeared

  FoundClientConnectingWait, // we successfully found a client, now we try to connect

  FoundClientConnectingTryNewTarget, // while we wait to connect, a new target became available and
  // we need to switch gears

  FoundClientConnectingStop, // while we wait to connect, a new target became available and we need
  // to stop

  Connected, // we connected on that client and we are fully established

  ConnectedStopping, // we were connected, but routing required us disconnect

  ConnectedStoppingPleaseReconnect, // we were connected, but routing required us disconnect, and
  // then changed its mind

  WaitingForDisconnect, // we are connect and a disconnect was issued to terminate the entire thing

  Failed, // an error happened and we can't recover from it
}
