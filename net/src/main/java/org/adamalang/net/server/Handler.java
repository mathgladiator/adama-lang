package org.adamalang.net.server;

import io.netty.buffer.ByteBuf;
import org.adamalang.common.net.ByteStream;
import org.adamalang.net.codec.ClientCodec;
import org.adamalang.net.codec.ClientMessage;
import org.adamalang.net.codec.ServerCodec;
import org.adamalang.net.codec.ServerMessage;

public class Handler implements ByteStream, ClientCodec.HandlerServer {
  private final ByteStream upstream;

  public Handler(ByteStream upstream) {
    this.upstream = upstream;
  }

  @Override
  public void request(int bytes) {
    // proxy to the appropriate thing
  }

  // IGNORE
  @Override
  public ByteBuf create(int bestGuessForSize) {
    return null;
  }

  @Override
  public void next(ByteBuf buf) {
    ClientCodec.route(buf, this);
  }

  @Override
  public void completed() {
    disconnected();
  }

  @Override
  public void error(int errorCode) {
    disconnected();
  }

  public void disconnected() {

  }

  @Override
  public void handle(ClientMessage.RequestInventoryHeartbeat payload) {

  }

  @Override
  public void handle(ClientMessage.RequestHeat payload) {

  }

  @Override
  public void handle(ClientMessage.StreamDisconnect payload) {

  }

  @Override
  public void handle(ClientMessage.StreamAttach payload) {

  }

  @Override
  public void handle(ClientMessage.StreamAskAttachmentRequest payload) {

  }

  @Override
  public void handle(ClientMessage.StreamUpdate payload) {

  }

  @Override
  public void handle(ClientMessage.StreamSend payload) {

  }

  @Override
  public void handle(ClientMessage.StreamConnect payload) {

  }

  @Override
  public void handle(ClientMessage.MeteringDeleteBatch payload) {

  }

  @Override
  public void handle(ClientMessage.MeteringBegin payload) {

  }

  @Override
  public void handle(ClientMessage.ScanDeploymentRequest payload) {

  }

  @Override
  public void handle(ClientMessage.ReflectRequest payload) {

  }

  @Override
  public void handle(ClientMessage.CreateRequest payload) {

  }

  @Override
  public void handle(ClientMessage.PingRequest payload) {
    ByteBuf buf = upstream.create(8);
    ServerCodec.write(buf, new ServerMessage.PingResponse());
    upstream.next(buf);
  }
}
