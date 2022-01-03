package org.adamalang.grpc.client.contracts;

public interface Remote {
  void canAttach(AskAttachmentCallback callback);

  void attach(String id, String name, String contentType, long size, String md5, String sha384, SeqCallback callback);

  void send(String channel, String marker, String message, SeqCallback callback);

  void disconnect();
}
