package org.adamalang.caravan.contracts;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;

public interface TranslateKeyService {
  public void lookup(Key key, Callback<Long> callback);
}
