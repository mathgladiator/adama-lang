package org.adamalang.grpc.client.contracts;

public interface SimpleEvents {
  /** the connection was successful, and we can talk to the document via the remote */
  public void connected();

  /** a data change has occurred */
  public void delta(String data);

  /** an error has occurred */
  public void error(int code);

  /** the document was disconnected */
  public void disconnected();
}
