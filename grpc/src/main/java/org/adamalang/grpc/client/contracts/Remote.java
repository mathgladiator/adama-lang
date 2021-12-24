package org.adamalang.grpc.client.contracts;

/** when a remote document is established, this controls how to interact with said document */
public interface Remote {
    /** ask if you can attach an asset */
    public void canAttach(AskAttachmentCallback callback);

    /** attach an asset to the document */
    public void attach(String id, String name, String contentType, long size, String md5, String sha384, SeqCallback callback);

    /** send a message to the document via the given channel and dedupe on the given marker */
    public void send(String channel, String marker, String message, SeqCallback callback);

    /** disconnect from the document */
    public void disconnect();
}
