/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
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
    ClientMessage.MeteringBegin meteringBegin = new ClientMessage.MeteringBegin();
    ClientMessage.MeteringDeleteBatch meteringDeleteBatch = new ClientMessage.MeteringDeleteBatch();
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
