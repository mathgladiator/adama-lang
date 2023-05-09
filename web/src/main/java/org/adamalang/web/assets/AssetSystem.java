/*
 * This file is subject to the terms and conditions outlined in the
 * file 'LICENSE' (hint: it's MIT-based) located in the root directory
 * near the README.md which you should also read. For more information
 * about the project which owns this file, see https://www.adama-platform.com/ .
 *
 * (c) 2020 - 2023 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;
import org.adamalang.web.io.ConnectionContext;

/** defines the asset system from the perspective of the web tier */
public interface AssetSystem {
  /** stream an asset from underlying medium */
  void request(AssetRequest request, AssetStream stream);

  /** stream out an asset */
  void request(Key key, NtAsset asset, AssetStream stream);

  /** attach the asset to the given document under the given principal */
  void attach(String identity, ConnectionContext context, Key key, NtAsset asset, String channel, String message, Callback<Integer> callback);

  /** upload an asset at the given key */
  void upload(Key key, NtAsset asset, AssetUploadBody body, Callback<Void> callback);
}
