/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
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
package org.adamalang.net.client.contracts.impl;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.client.LocalRegionClientMetrics;
import org.adamalang.runtime.sys.capacity.HeatMonitor;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;

public class CallbackByteStreamInfo implements Callback<ByteStream> {
  private final HeatMonitor monitor;
  private final LocalRegionClientMetrics metrics;

  public CallbackByteStreamInfo(HeatMonitor monitor, LocalRegionClientMetrics metrics) {
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
