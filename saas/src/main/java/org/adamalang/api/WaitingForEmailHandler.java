package org.adamalang.api;


public interface WaitingForEmailHandler {
  public void handle(InitRevokeAllRequest request, SimpleResponder responder);

  public void handle(InitGenerateNewKeyPairRequest request, PrivateKeyResponder responder);

public void disconnect(long id);}
