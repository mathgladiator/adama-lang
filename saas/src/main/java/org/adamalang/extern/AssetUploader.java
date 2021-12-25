package org.adamalang.extern;

import org.adamalang.runtime.contracts.Callback;

import java.io.File;

public interface AssetUploader {
    public void upload(String id, File file, Callback<Boolean> callback);
}
