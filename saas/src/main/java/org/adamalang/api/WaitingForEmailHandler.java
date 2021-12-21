package org.adamalang.api;


public interface WaitingForEmailHandler {
public void bind();

  public void handle(InitRevokeAllRequest request, SimpleResponder responder);

  public void handle(InitGenerateIdentityRequest request, InitiationResponder responder);

public void disconnect(long id);

}
