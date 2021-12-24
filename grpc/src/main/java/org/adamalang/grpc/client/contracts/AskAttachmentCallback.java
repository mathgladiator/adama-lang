package org.adamalang.grpc.client.contracts;

/** asking whether or not attachments are allowed results in a simple signal or some failure */
public interface AskAttachmentCallback {
    /** attachments are allowed... for you */
    public void allow();

    /** attachments are not allowed... by you */
    public void reject();

    /** we couldn't ask */
    public void error(int code);
}
