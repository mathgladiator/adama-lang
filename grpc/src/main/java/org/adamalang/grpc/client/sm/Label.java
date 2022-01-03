package org.adamalang.grpc.client.sm;

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

  WaitingForDisconnect, // TODO

  WaitingForDisconnectCancelTryAgain, // TODO
}
