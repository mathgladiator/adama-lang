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
package org.adamalang.net.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

public class ClientCodecTests {
  @Test
  public void flow() {
    ClientMessage.PingRequest pingRequest = new ClientMessage.PingRequest();
    ClientMessage.CreateRequest createRequest = new ClientMessage.CreateRequest();
    ClientMessage.ReflectRequest reflectRequest = new ClientMessage.ReflectRequest();
    ClientMessage.ScanDeployment scanDeployment = new ClientMessage.ScanDeployment();
    ClientMessage.StreamConnect streamConnect = new ClientMessage.StreamConnect();
    ClientMessage.StreamSend streamSend = new ClientMessage.StreamSend();
    ClientMessage.StreamUpdate streamUpdate = new ClientMessage.StreamUpdate();
    ClientMessage.StreamDisconnect streamDisconnect = new ClientMessage.StreamDisconnect();
    ClientMessage.StreamAskAttachmentRequest streamAskAttachmentRequest = new ClientMessage.StreamAskAttachmentRequest();
    ClientMessage.StreamAttach streamAttach = new ClientMessage.StreamAttach();
    ClientMessage.RequestHeat requestHeat = new ClientMessage.RequestHeat();
    ClientMessage.RequestInventoryHeartbeat requestInventoryHeartbeat = new ClientMessage.RequestInventoryHeartbeat();

    /*
    ByteBuf buf = Unpooled.buffer();
    ClientCodec.write(buf, pingRequest);
    ClientCodec.write(buf, createRequest);
    ClientCodec.write(buf, reflectRequest);
    ClientCodec.write(buf, scanDeployment);
    ClientCodec.write(buf, meteringBegin);
    ClientCodec.write(buf, meteringDeleteBatch);
    ClientCodec.write(buf, streamConnect);
    ClientCodec.write(buf, streamSend);
    ClientCodec.write(buf, streamUpdate);
    ClientCodec.write(buf, streamDisconnect);
    ClientCodec.write(buf, streamAskAttachmentRequest);
    ClientCodec.write(buf, streamAttach);
    ClientCodec.write(buf, requestHeat);
    ClientCodec.write(buf, requestInventoryHeartbeat);
    */
  }
}
