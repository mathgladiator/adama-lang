/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See https://www.adama-platform.com/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber ( http://jeffrey.io )
 */
package org.adamalang.web.assets;

import org.adamalang.common.Callback;
import org.adamalang.runtime.data.Key;
import org.adamalang.runtime.natives.NtAsset;

/** interface to upload an asset */
public interface AssetUploader {

  /** upload an asset at the given key */
  void upload(Key key, NtAsset asset, AssetUploadBody body, Callback<Void> callback);
}
