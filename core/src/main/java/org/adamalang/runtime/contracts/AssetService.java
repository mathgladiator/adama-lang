package org.adamalang.runtime.contracts;

import org.adamalang.runtime.natives.NtAsset;

/** the contract for the asset service */
public interface AssetService {

  /** upload a single asset */
  public void upload(AssetRequest request, Callback<NtAsset> callback);
}
