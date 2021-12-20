package org.adamalang.api;


public interface WaitingForEmailHandler {
  public void handle(InitGenerateNewKeyPairRequest request, SimpleResponder responder);

  public void handle(InitRevokeAllRequest request, SimpleResponder responder);

public void disconnect(long id);}
