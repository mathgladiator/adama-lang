/* The Adama Programming Language For Board Games!
 *    See http://www.adama-lang.org/ for more information.
 * (c) copyright 2020 Jeffrey M. Barber (http://jeffrey.io) */
package org.adamalang.netty.server;

import org.adamalang.netty.contracts.ServerOptions;

public class CliServerOptions implements ServerOptions {
  private final String healthCheckPath;
  private final int maxContentLengthSize;
  private final int maxWebSocketFrameSize;
  private final int port;
  private final int timeoutWebsocketHandshake;
  private final String websocketPath;

  public CliServerOptions(final String... args) {
    var _port = 8080;
    var _maxWebSocketFrameSize = 65536;
    var _maxContentLengthSize = 65536;
    var _timeoutWebsocketHandshake = 2500;
    var _websocketPath = "/~socket";
    var _healthCheckPath = "/~health_check_lb";
    for (var k = 0; k + 1 < args.length; k++) {
      final var key = args[k].trim().toLowerCase();
      final var value = args[k + 1];
      switch (key) {
        case "--port":
          _port = Integer.parseInt(value);
          break;
        case "--wss-frame-size":
          _maxWebSocketFrameSize = Integer.parseInt(value);
          break;
        case "--http-max-content-length":
          _maxContentLengthSize = Integer.parseInt(value);
          break;
        case "--wss-timeout-handshake":
          _timeoutWebsocketHandshake = Integer.parseInt(value);
          break;
        case "--wss-path":
          _websocketPath = value;
          break;
        case "--health-check-path":
          _healthCheckPath = value;
          break;
      }
    }
    if (_port < 1 || _port > 65535) { throw new RuntimeException(String.format("Port must be between 1 and 65535. Given=`%d`.", _port)); }
    port = _port;
    if (_maxContentLengthSize < 1024) { throw new RuntimeException(String.format("Max content length must be greater than 1024. Given=`%d`", _maxContentLengthSize)); }
    maxContentLengthSize = _maxContentLengthSize;
    if (_maxWebSocketFrameSize < 1024) { throw new RuntimeException(String.format("WebSocket max frame size must be greater than 1024. Given=`%d`", _maxWebSocketFrameSize)); }
    maxWebSocketFrameSize = _maxWebSocketFrameSize;
    timeoutWebsocketHandshake = _timeoutWebsocketHandshake;
    if (_timeoutWebsocketHandshake < 500) { throw new RuntimeException(String.format("WebSocket handshake must be greater than 500ms. Given=`%d`", _timeoutWebsocketHandshake)); }
    if (!_websocketPath.startsWith("/")) { throw new RuntimeException(String.format("WebSocket path must start with forward slash. Given=`%s`", _websocketPath)); }
    websocketPath = _websocketPath;
    if (!_healthCheckPath.startsWith("/")) { throw new RuntimeException(String.format("Health check path must start with forward slash. Given=`%s`", _healthCheckPath)); }
    healthCheckPath = _healthCheckPath;
  }

  @Override
  public String healthCheckPath() {
    return healthCheckPath;
  }

  @Override
  public int maxContentLengthSize() {
    return maxContentLengthSize;
  }

  @Override
  public int maxWebSocketFrameSize() {
    return maxWebSocketFrameSize;
  }

  @Override
  public int port() {
    return port;
  }

  @Override
  public int timeoutWebsocketHandshake() {
    return timeoutWebsocketHandshake;
  }

  @Override
  public String websocketPath() {
    return websocketPath;
  }
}