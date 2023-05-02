/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.net.client.contracts.impl;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.client.ClientMetrics;
import org.adamalang.net.client.contracts.HeatMonitor;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;

public class CallbackByteStreamInfo implements Callback<ByteStream> {
  private final HeatMonitor monitor;
  private final ClientMetrics metrics;

  public CallbackByteStreamInfo(HeatMonitor monitor, ClientMetrics metrics) {
    this.monitor = monitor;
    this.metrics = metrics;
  }

  @Override
  public void success(ByteStream stream) {
    if (monitor != null) {
      ByteBuf monitorWrite = stream.create(8);
      ClientCodec.write(monitorWrite, new ClientMessage.RequestHeat());
      stream.next(monitorWrite);
    }
    ByteBuf requestInventoryWrite = stream.create(8);
    ClientCodec.write(requestInventoryWrite, new ClientMessage.RequestInventoryHeartbeat());
    stream.next(requestInventoryWrite);
  }

  @Override
  public void failure(ErrorCodeException ex) {
    metrics.client_info_failed_ask.run();
  }
}
