package org.adamalang.extern;

import org.adamalang.common.Callback;
import org.adamalang.runtime.natives.NtAsset;

import java.io.File;

public interface AssetUploader {
    public void upload(NtAsset asset, File localFile, Callback<Boolean> callback);
}
