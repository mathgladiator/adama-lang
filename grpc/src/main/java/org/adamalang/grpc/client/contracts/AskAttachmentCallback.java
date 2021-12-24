package org.adamalang.grpc.client.contracts;

public interface AskAttachmentCallback {
    public void allow();
    public void reject();
    public void error(int code);
}
