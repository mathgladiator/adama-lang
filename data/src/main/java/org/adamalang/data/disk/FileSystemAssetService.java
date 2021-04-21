/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE'
 * which is in the root directory of the repository. This file is part of the 'Adama'
 * project which is a programming language and document store for board games.
 * 
 * See http://www.adama-lang.org/ for more information.
 * 
 * (c) 2020 - 2021 by Jeffrey M. Barber (http://jeffrey.io)
*/
package org.adamalang.data.disk;

import org.adamalang.runtime.contracts.AssetRequest;
import org.adamalang.runtime.contracts.AssetService;
import org.adamalang.runtime.contracts.Callback;
import org.adamalang.runtime.natives.NtAsset;

import java.io.File;

public class FileSystemAssetService implements AssetService {
  public final File root;
  public FileSystemAssetService(File root) {
    this.root = root;
  }

  @Override
  public void upload(AssetRequest request, Callback<NtAsset> callback) {

    // create the path, and blah
  }
}
