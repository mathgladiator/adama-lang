package org.adamalang.grpc.client;

import org.adamalang.grpc.client.active.Directory;
import org.adamalang.grpc.client.contracts.CreateCallback;
import org.adamalang.grpc.client.contracts.Events;
import org.adamalang.grpc.client.contracts.Remote;
import org.adamalang.runtime.contracts.Key;
import org.adamalang.runtime.natives.NtClient;

public class Sketch {
  public static void main(String[] args) {
    //
    Directory directory = new Directory(4);
    directory.connect(NtClient.NO_ONE, new Key("space", "key"), new Events() {
      @Override
      public void connected(Remote remote) {

      }

      @Override
      public void delta(String data) {

      }

      @Override
      public void error(int code) {

      }

      @Override
      public void disconnected() {

      }
    });

    directory.create(NtClient.NO_ONE, new Key("space", "key"), null, "{}", new CreateCallback() {
      @Override
      public void created() {

      }

      @Override
      public void error(int code) {

      }
    });
  }
}
