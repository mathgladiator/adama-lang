/*
* Adama Platform and Language
* Copyright (C) 2021 - 2024 by Adama Platform Engineering, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
