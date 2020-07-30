/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.contracts;

public interface ServerOptions {
  public String healthCheckPath();
  public int maxContentLengthSize();
  public int maxWebSocketFrameSize();
  public int port();
  public int timeoutWebsocketHandshake();
  public String websocketPath();
}
