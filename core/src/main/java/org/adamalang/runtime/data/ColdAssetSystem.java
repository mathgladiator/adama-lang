/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
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
package org.adamalang.runtime.data;

import org.adamalang.common.Callback;

import java.util.List;

/** list assets for an object that are stored; this is for garbage collection of assets */
public interface ColdAssetSystem {

  /** list all the asset ids for a given key */
  void listAssetsOf(Key key, Callback<List<String>> callback);

  /** delete the asset for a given document */
  void deleteAsset(Key key, String assetId, Callback<Void> callback);
}
