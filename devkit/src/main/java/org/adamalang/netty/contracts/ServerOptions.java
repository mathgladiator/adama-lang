/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.netty.contracts;

public interface ServerOptions {
  public String healthCheckPath();
  public int maxContentLengthSize();
  public int maxWebSocketFrameSize();
  public int port();
  public int timeoutWebsocketHandshake();
  public String websocketPath();
}
