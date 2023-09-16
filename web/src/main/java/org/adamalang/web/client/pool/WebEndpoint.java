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
package org.adamalang.web.client.pool;

import java.net.URI;
import java.util.Objects;

/** and endpoint representing a single instance of a remote box */
public class WebEndpoint {
  public final boolean secure;
  public final String host;
  public final int port;

  public WebEndpoint(URI uri) {
    this.secure = uri.getScheme().equals("https");
    this.port = uri.getPort() < 0 ? (secure ? 443 : 80) : uri.getPort();
    this.host = uri.getHost();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WebEndpoint that = (WebEndpoint) o;
    return secure == that.secure && port == that.port && Objects.equals(host, that.host);
  }

  @Override
  public int hashCode() {
    return Objects.hash(secure, host, port);
  }
}
