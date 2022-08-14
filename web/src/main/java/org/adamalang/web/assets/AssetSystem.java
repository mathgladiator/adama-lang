package org.adamalang.web.assets;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.io.ConnectionContext;

/** defines the asset system from the perspective of the web tier */
public interface AssetSystem {

  /** attach the asset to the given document under the given principal */
  public void attach(String identity, ConnectionContext context, Key key, NtAsset asset, Callback<Integer> callback);
}
