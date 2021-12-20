package org.adamalang.api;


public interface DocumentStreamHandler {
  public void handle(ConnectionSendRequest request, SimpleResponder responder);

  public void handle(ConnectionEndRequest request, SimpleResponder responder);

public void disconnect(long id);}
