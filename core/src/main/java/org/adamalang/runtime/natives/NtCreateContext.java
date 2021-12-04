package org.adamalang.runtime.natives;

/** context of a given policy decision; this a virtual message holder which is processed in DefineDocumentEvent */
public class NtCreateContext {
    // for browsers, what is the origin of the page making the request
    public final String origin;

    // what is the IP address of the client
    public final String ip;

    // the key being created
    public final String key;

    public NtCreateContext(String origin, String ip, String key) {
        this.origin = origin;
        this.ip = ip;
        this.key = key;
    }
}
