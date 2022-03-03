package org.adamalang.common.net;

import org.adamalang.common.ErrorCodeException;

/** each connection has a separate lifecycle from birth to death*/
public interface Lifecycle {
  public void connected(ChannelClient channel);

  public void failed(ErrorCodeException ex);

  public void disconnected();
}
