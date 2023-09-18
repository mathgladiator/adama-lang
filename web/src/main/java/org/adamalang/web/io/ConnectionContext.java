/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang.web.io;

import java.util.TreeMap;
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
  public final TreeMap<String, String> identities;

  public ConnectionContext(String origin, String remoteIp, String userAgent, String assetKey, TreeMap<String, String> identities) {
    this.origin = origin != null ? origin : "";
    this.remoteIp = remoteIpFix(remoteIp);
    this.userAgent = userAgent != null ? userAgent : "";
    this.assetKey = assetKey;
    this.identities = identities;
  }

  public String identityOf(String identityRaw) {
    if (identityRaw.startsWith("cookie:")) {
      if (identities != null) {
        return identities.get(identityRaw.substring(7));
      }
    }
    return identityRaw;
  }

  /** we don't care about the port and null values */
  public static String remoteIpFix(String remoteIp) {
    if (remoteIp == null) {
      return "";
    }
    return remoteIp.split(Pattern.quote(":"))[0];
  }
}
