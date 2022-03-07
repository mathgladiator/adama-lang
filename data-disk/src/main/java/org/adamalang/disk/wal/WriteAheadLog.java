package org.adamalang.disk.wal;

import org.adamalang.common.Callback;

public interface WriteAheadLog {

  public void write(WriteAheadMessage message, Callback<Void> callback);
}
