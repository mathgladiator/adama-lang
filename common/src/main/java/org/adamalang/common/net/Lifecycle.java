package org.adamalang.common.net;

import org.adamalang.common.ErrorCodeException;

public interface Lifecycle {
  public void connected(ChannelClient channel);

  public void failed(ErrorCodeException ex);

  public void disconnected();
}
