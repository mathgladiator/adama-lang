package org.adamalang.api;


public interface AttachmentUploadHandler {
  public void handle(AttachmentAppendRequest request, SimpleResponder responder);

  public void handle(AttachmentFinishRequest request, SimpleResponder responder);

public void disconnect(long id);}
