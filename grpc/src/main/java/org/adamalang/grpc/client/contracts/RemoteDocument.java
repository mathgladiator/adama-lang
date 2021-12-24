package org.adamalang.grpc.client.contracts;

public interface RemoteDocument {
    public void canAttach(AskAttachmentCallback callback);

    public void attach(String id, String name, String contentType, long size, String md5, String sha384, SeqCallback callback);

    public void send(String channel, String marker, String message, SeqCallback callback);

    public void disconnect();
}
