/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.net.client.bidi;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.Callback;
import org.adamalang.common.ErrorCodeException;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.client.contracts.MeteringStream;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;

public class MeteringExchange extends ServerCodec.StreamMetering implements Callback<ByteStream> {
  private static final ClientMessage.MeteringBegin COMMON_BEGIN = new ClientMessage.MeteringBegin();
  private final String target;
  private final MeteringStream metering;
  private ByteStream upstream;
  private String batchToRemove;

  public MeteringExchange(String target, MeteringStream metering) {
    this.target = target;
    this.metering = metering;
    this.batchToRemove = null;
  }

  @Override
  public void success(ByteStream upstream) {
    this.upstream = upstream;
    begin();
  }

  @Override
  public void failure(ErrorCodeException ex) {
    metering.failure(ex.code);
  }

  private void begin() {
    ByteBuf buf = upstream.create(4);
    ClientCodec.write(buf, COMMON_BEGIN);
    upstream.next(buf);
  }

  @Override
  public void completed() {
    metering.finished();
  }

  @Override
  public void error(int errorCode) {
    metering.failure(errorCode);
  }

  @Override
  public void handle(ServerMessage.MeteringBatchRemoved payload) {
    metering.handle(target, batchToRemove, this::begin);
  }

  @Override
  public void handle(ServerMessage.MeteringBatchFound payload) {
    batchToRemove = payload.batch;
    ByteBuf buf = upstream.create(4 + payload.id.length());
    ClientMessage.MeteringDeleteBatch deleteBatch = new ClientMessage.MeteringDeleteBatch();
    deleteBatch.id = payload.id;
    ClientCodec.write(buf, deleteBatch);
    upstream.next(buf);
  }
}
