/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (it's dual licensed) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2021 - 2023 by Adama Platform Initiative, LLC
 */
package org.adamalang.web.io;

import java.util.regex.Pattern;

/**
 * Since WebSocket is the primary transport, we can leverage some HTTP headers for insight along
 * with connection properties
 */
public class ConnectionContext {
  public final String origin;
  public final String remoteIp;
  public final String userAgent;
  public final String assetKey;

  public ConnectionContext(String origin, String remoteIp, String userAgent, String assetKey) {
    this.origin = origin != null ? origin : "";
    this.remoteIp = remoteIpFix(remoteIp);
    this.userAgent = userAgent != null ? userAgent : "";
    this.assetKey = assetKey;
  }

  /** we don't care about the port and null values */
  public static String remoteIpFix(String remoteIp) {
    if (remoteIp == null) {
      return "";
    }
    return remoteIp.split(Pattern.quote(":"))[0];
  }
}
