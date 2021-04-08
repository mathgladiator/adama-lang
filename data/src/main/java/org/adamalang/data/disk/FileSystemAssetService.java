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
