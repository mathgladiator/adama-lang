/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * The 'LICENSE' file is in the root directory of the repository. Hint: it is MIT.
 * 
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
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
