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
