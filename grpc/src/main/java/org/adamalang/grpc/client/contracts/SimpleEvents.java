package org.adamalang.grpc.client.contracts;

public interface SimpleEvents {
  /** the connection was successful, and we can talk to the document via the remote */
  void connected();

  /** a data change has occurred */
  void delta(String data);

  /** an error has occurred */
  void error(int code);

  /** the document was disconnected */
  void disconnected();
}
