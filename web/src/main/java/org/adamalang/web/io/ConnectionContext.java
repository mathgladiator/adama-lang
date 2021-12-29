/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.web.io;

import java.util.regex.Pattern;

/** Since WebSocket is the primary transport, we can leverage some HTTP headers for insight along with connection properties */
public class ConnectionContext {
    public final String origin;
    public final String remoteIp;
    public final String userAgent;

    public ConnectionContext(String origin, String remoteIp, String userAgent) {
        this.origin = origin != null ? origin : "";
        this.remoteIp = remoteIpFix(remoteIp);
        this.userAgent = userAgent != null ? userAgent : "";
    }

    /** we don't care about the port and null values */
    public static String remoteIpFix(String remoteIp) {
        if (remoteIp == null) {
            return "";
        }
        return remoteIp.split(Pattern.quote(":"))[0];
    }
}
